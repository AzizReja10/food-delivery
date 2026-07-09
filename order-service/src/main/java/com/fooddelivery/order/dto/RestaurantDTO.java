package com.fooddelivery.order.dto;

import lombok.Data;

@Data
public class RestaurantDTO {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Boolean isOpen;
}