package com.fooddelivery.delivery.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDeliveredEvent {
    private Long orderId;
    private Long customerId;
    private Long partnerId;
    private LocalDateTime deliveredAt;
}
