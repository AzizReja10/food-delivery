package com.fooddelivery.restaurant.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemResponse {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isVeg;
}
