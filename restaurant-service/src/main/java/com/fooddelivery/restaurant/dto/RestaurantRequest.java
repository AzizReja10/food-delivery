package com.fooddelivery.restaurant.dto;

import com.fooddelivery.restaurant.entity.CuisineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String imageUrl;

    @NotNull(message = "Cuisine type is required")
    private CuisineType cuisineType;
}