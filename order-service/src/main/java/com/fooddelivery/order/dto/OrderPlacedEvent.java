package com.fooddelivery.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private String restaurantName;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private LocalDateTime createdAt;
}
