package com.shopsphere.product_service.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.shopsphere.product_service.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>  {
     List<Product> findByCategory(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
}