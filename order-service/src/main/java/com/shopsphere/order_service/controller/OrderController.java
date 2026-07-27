package com.shopsphere.order_service.controller;

import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.order_service.entity.Order;
import com.shopsphere.order_service.security.JwtUtil;
import com.shopsphere.order_service.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;



@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService; 
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order,
            @RequestHeader("Authorization") String authHeader) {
                String token = jwtUtil.extractTokenFromHeader(authHeader);
                String email = jwtUtil.extractEmail(token);
                order.setEmail(email);

                Order created = orderService.createOrder(order);
                return ResponseEntity.ok(created);
    }
    
    
}
