package com.fooddelivery.order.client;

import com.fooddelivery.order.dto.MenuItemDTO;
import com.fooddelivery.order.dto.RestaurantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service",url = "${app.restaurant-service.url}")
public interface RestaurantClient {
    @GetMapping("/restaurants/internal/menu/{itemId}")
    MenuItemDTO getMenuItemById(@PathVariable Long itemId);
    @GetMapping("/restaurants/{id}")
    RestaurantDTO getRestaurantById(@PathVariable Long id);
}
