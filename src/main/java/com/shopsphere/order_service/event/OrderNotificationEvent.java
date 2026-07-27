package com.shopsphere.order_service.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderNotificationEvent {
    private Long orderId;
    private String email;
    private String productName;
    private int quantity;
    private String status;
    
}
