package com.shopsphere.order_service.service;

import com.shopsphere.order_service.OrderServiceApplication;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.shopsphere.order_service.entity.Order;
import com.shopsphere.order_service.entity.OrderStatus;
import com.shopsphere.order_service.event.OrderEvent;
import com.shopsphere.order_service.event.StockUpdateEvent;
import com.shopsphere.order_service.repository.OrderRepository;

import org.springframework.transaction.annotation.Transactional;
import com.shopsphere.order_service.event.OrderNotificationEvent;




@Service
public class OrderService {
    

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    @Autowired
    private KafkaTemplate<String, OrderNotificationEvent> notificationKafkaTemplate; 

    

    public Order createOrder(Order order){
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        Order saveOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent(saveOrder.getId(),saveOrder.getProductId(),saveOrder.getQuantity());
        kafkaTemplate.send("order-events",event);
        return saveOrder;
    }

    //this will read the response from the product service reduce stock according to that
    //it will change the state cancelled or success
    @Transactional
    public void updateOrderStatus(Long orderId , boolean success){
        Order order = orderRepository.findById(orderId)
               .orElseThrow(() -> new RuntimeException("order not found" + orderId));

        if(success){
            order.setStatus(OrderStatus.CONFIRMED);
        }else{
            order.setStatus(OrderStatus.CANCELLED);
        }
        orderRepository.save(order);
        OrderNotificationEvent notification = new OrderNotificationEvent(
            order.getId(),
            order.getEmail(),
            order.getProductName(),
            order.getQuantity(),
            order.getStatus().name()
        );
    notificationKafkaTemplate.send("order-notifications", notification);
}
    @KafkaListener(topics = "stock-update-events", groupId = "order-service-group")
    public void handleStockUpdate(StockUpdateEvent event){
        updateOrderStatus(event.getOrderId(), event.isSuccess());
    }
}
 