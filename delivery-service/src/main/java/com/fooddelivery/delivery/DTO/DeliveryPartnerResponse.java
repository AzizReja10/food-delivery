package com.fooddelivery.delivery.DTO;

import lombok.Data;

@Data
public class DeliveryPartnerResponse {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private Boolean isAvailable;
    private Integer totalDeliveries;
}
