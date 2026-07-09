package com.fooddelivery.restaurant.dto;

import com.fooddelivery.restaurant.entity.CuisineType;
import lombok.Data;

@Data
public class RestaurantRequest {
    private String name;
    private String address;
    private String city;
    private String phone;
    private String imageUrl;
    private CuisineType cuisineType;
}
