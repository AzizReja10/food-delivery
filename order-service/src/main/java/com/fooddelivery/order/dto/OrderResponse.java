package com.fooddelivery.order.dto;

import com.fooddelivery.order.entity.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long customerId;
    private Long restaurantId;
    private String restaurantName;
    private String deliveryAddress;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String specialInstructions;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
}