package com.shopsphere.order_service.entity;
import java.math.BigDecimal;
import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "order_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal priceAtOrderTime;
    private LocalDateTime orderDate;
    private String email;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
