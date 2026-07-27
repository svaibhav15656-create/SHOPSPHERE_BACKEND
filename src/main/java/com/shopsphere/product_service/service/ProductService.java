package com.shopsphere.product_service.service;
import java.util.*;

import org.springframework.data.redis.core.RedisTemplate;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;

import com.shopsphere.product_service.entity.ProcessedOrder;
import com.shopsphere.product_service.entity.Product;
import com.shopsphere.product_service.event.OrderEvent;
import com.shopsphere.product_service.event.StockUpdateEvent;
import com.shopsphere.product_service.repository.ProcessedOrderRepository;
import com.shopsphere.product_service.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import com.shopsphere.product_service.exception.InsufficientStockException;
import com.shopsphere.product_service.exception.ProductLockedException;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    KafkaTemplate<String,StockUpdateEvent> kafkaTemplate;
    @Autowired
    private ProcessedOrderRepository processedOrderRepository;

    public Product createProduct(Product product){
        return productRepository.save(product);
    }
    @Cacheable(value ="products" , key = "#id")
    public Product getProductById(Long id){
       Optional<Product> product = productRepository.findById(id);
       if(product.isPresent()){
        return product.get();
       }
       return null;
    }
    public  Page<Product> getAllProducts(Pageable pageable){
        return productRepository.findAll(pageable);
    }
    @CacheEvict(value = "products", key = "#id")
    public Product updateProduct(Long id, Product updatedProduct){
    Optional<Product> existingProductOpt = productRepository.findById(id);
    if(existingProductOpt.isPresent()){
        Product existingProduct = existingProductOpt.get();
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
        existingProduct.setCategory(updatedProduct.getCategory());
        return productRepository.save(existingProduct);   
    }
    return null; 
}
    @CacheEvict(value = "products", key = "#id")
    public boolean deleteProduct(Long id){
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            productRepository.deleteById(id);
            return true;
        }
        return false; 
    }
    public List<Product>  searchByCategory(String category){
        return productRepository.findByCategory(category);
    }
    public List<Product> searchByName(String name){
        return productRepository.findByNameContainingIgnoreCase(name);
    }

@Transactional
@CacheEvict(value = "products", key = "#id")
public Product reduceStock(Long id, int quantity){
    String lockKey = "lock:product:"+id;
    //redis lock distribution
        //line below has true or false in lockAcuried if lock is there setIfAbsent will return fasle and if block will execute 
        //otherwise setIFabsent will return true if locked in not sent and setIfabsent and set it so lockacquied will have true value
        //try block will excuted
    Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey,"LOCKED" ,Duration.ofSeconds(5));
    if(Boolean.FALSE.equals(lockAcquired)){
        throw new ProductLockedException("Product is currently locked, try again");
    }
    try{
        Optional<Product> productOpt = productRepository.findById(id);
        if(productOpt.isEmpty()){
            throw new RuntimeException("Product not found");
        }       
        Product product = productOpt.get();
        if(product.getStockQuantity() < quantity){
            throw new InsufficientStockException("Insufficient stock");
        }
        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }finally{
        redisTemplate.delete(lockKey);
    }
}
  

@RetryableTopic(
    attempts = "4",
    backoff = @Backoff(delay = 3000, multiplier = 2.0),
    include = { ProductLockedException.class },
    dltStrategy = DltStrategy.FAIL_ON_ERROR
)
@Transactional
@KafkaListener(topics = "order-events", groupId = "product-service-group")
public void handleOrderCreated(OrderEvent event){
    try {
        processedOrderRepository.save(new ProcessedOrder(event.getOrderId()));
        reduceStock(event.getProductId(), event.getQuantity());
        kafkaTemplate.send("stock-update-events",
            new StockUpdateEvent(event.getOrderId(), true, "Stock reduced successfully"));
    } catch (DataIntegrityViolationException dup) {
        // duplicate delivery, safely ignored
    } catch (ProductLockedException lockEx) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        throw lockEx;   // rethrow so @RetryableTopic can catch it and trigger a retry
    } catch (RuntimeException ex) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        kafkaTemplate.send("stock-update-events",
            new StockUpdateEvent(event.getOrderId(), false, ex.getMessage()));
    }
    }
    @KafkaListener(topics = "order-events-dlt", groupId = "product-service-group")
    public void handleOrderCreatedDlt(OrderEvent event){
        kafkaTemplate.send("stock-update-events",
            new StockUpdateEvent(event.getOrderId(), false, "Failed after multiple retries: product still locked"));
}
}