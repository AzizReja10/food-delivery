package com.fooddelivery.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private String restaurantName;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private LocalDateTime createdAt;
}
