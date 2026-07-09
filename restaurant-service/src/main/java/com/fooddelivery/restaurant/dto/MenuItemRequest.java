package com.fooddelivery.restaurant.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data

public class MenuItemRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Boolean isVeg;
}
