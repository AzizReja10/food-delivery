package com.fooddelivery.restaurant.dto;

import com.fooddelivery.restaurant.entity.CuisineType;
import lombok.Data;

@Data
public class RestaurantResponse {
    private Long id;
    private String name;
    private Long ownerId;
    private String address;
    private String city;
    private String phone;
    private String imageUrl;
    private CuisineType cuisineType;
    private Boolean isOpen;
    private Double rating;
}
