package com.fooddelivery.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemDTO {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private String name;
    private BigDecimal price;
    private Boolean isAvailable;
}
