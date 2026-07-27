package com.shopsphere.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.product_service.entity.ProcessedOrder;

public interface ProcessedOrderRepository extends JpaRepository<ProcessedOrder,Long> {

    
}
