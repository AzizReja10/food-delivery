package com.fooddelivery.delivery.DTO;

import com.fooddelivery.delivery.entity.DeliveryStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryResponse {
    private Long id;
    private Long orderId;
    private Long partnerId;
    private Long customerId;
    private Long restaurantId;
    private String restaurantName;
    private String deliveryAddress;
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
}
