package com.shopsphere.order_service.repository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.shopsphere.order_service.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
   
}