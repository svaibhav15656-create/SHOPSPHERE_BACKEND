package com.shopsphere.product_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.product_service.entity.Product;
import com.shopsphere.product_service.service.ProductService;
import java.util.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;






@RestController

@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product>createProduct(@RequestBody Product product) {
       return ResponseEntity.ok(productService.createProduct(product));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    Product product = productService.getProductById(id);
    if(product == null){
        return ResponseEntity.notFound().build();
        
    }
    return ResponseEntity.ok(product);
}
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }
   @PutMapping("/{id}")
public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
    Product updatedProduct = productService.updateProduct(id, product);
    if(updatedProduct == null){
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updatedProduct);
}
    @DeleteMapping("/{id}")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    boolean deleted = productService.deleteProduct(id);
    if(deleted){
        return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();
}


@GetMapping("/search/category/{category}")
public ResponseEntity<List<Product>>  searchByCategory(@PathVariable String category) {
    return ResponseEntity.ok(productService.searchByCategory(category));
}
@GetMapping("/search/name")
public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
    return ResponseEntity.ok(productService.searchByName(name));
}
@PutMapping("/{id}/reduce-stock")
public ResponseEntity<Product> reduceStock(@PathVariable Long id, @RequestParam int quantity) {
    Product product = productService.reduceStock(id, quantity);
    return ResponseEntity.ok(product);
}

}