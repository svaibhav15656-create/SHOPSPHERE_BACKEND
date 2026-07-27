package com.shopsphere.notification_service.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.shopsphere.notification_service.event.OrderNotificationEvent;
import com.shopsphere.notification_service.repository.NotificationRepository;
import com.shopsphere.notification_service.entity.Notification;
@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);


    @Autowired
    private NotificationRepository notificationRepository;


    @KafkaListener(topics = "order-notifications", groupId = "notification-service-group")
    public void handleOrderNotification(OrderNotificationEvent event){
        logger.info("Sending email to {} - Order #{} ({} x {}) is now {}",
    event.getEmail(),
    event.getOrderId(),
    event.getQuantity(),
    event.getProductName(),
    event.getStatus());

    Notification notification = new Notification(null,event.getOrderId(),event.getEmail(),event.getStatus(), LocalDateTime.now());
    notificationRepository.save(notification);
    }
    
}
