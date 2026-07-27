package com.shopsphere.product_service;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import com.shopsphere.product_service.entity.Product;
import com.shopsphere.product_service.event.OrderEvent;
import com.shopsphere.product_service.repository.ProductRepository;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order-events", "stock-update-events"})
@ActiveProfiles("test")
public class ProductServiceKafkaTest {

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    @Autowired
    private ProductRepository productRepository;

    private Long productId;

    @Test
    void whenOrderCreated_stockShouldReduce(){
        Product product = new Product();
        product.setName("Test Laptop");
        product.setStockQuantity(10);
        product = productRepository.save(product);
        productId = product.getId();

        OrderEvent event = new OrderEvent(999L, productId, 3);
        kafkaTemplate.send("order-events", event);

        await().atMost(Duration.ofSeconds(10))
               .untilAsserted(() -> {
                   Product updated = productRepository.findById(productId).orElseThrow();
                   assertEquals(7, updated.getStockQuantity());
               });
    }
//didnt reduced 
    @AfterEach
    void cleanup(){
        if (productId != null) {
            productRepository.deleteById(productId);
        }
    }
}