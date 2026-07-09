package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.dto.*;
import com.fooddelivery.restaurant.entity.CuisineType;
import com.fooddelivery.restaurant.dto.MenuItemRequest;
import com.fooddelivery.restaurant.dto.MenuItemResponse;
import com.fooddelivery.restaurant.dto.RestaurantResponse;
import com.fooddelivery.restaurant.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // ── Restaurant endpoints ─────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @RequestBody RestaurantRequest request,
            @RequestHeader("X-User-ID") Long ownerId) {
        return new ResponseEntity<>(
                restaurantService.createRestaurant(request, ownerId),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurant());
    }

    @GetMapping("/open")
    public ResponseEntity<List<RestaurantResponse>> getOpenRestaurants() {
        return ResponseEntity.ok(restaurantService.getOpenRestaurant());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<RestaurantResponse>> getByCity(
            @PathVariable String city) {
        return ResponseEntity.ok(restaurantService.getRestaurantByCity(city));
    }

    @GetMapping("/cuisine/{cuisineType}")
    public ResponseEntity<List<RestaurantResponse>> getByCuisine(
            @PathVariable CuisineType cuisineType) {
        return ResponseEntity.ok(
                restaurantService.getRestaurantByCuisine(cuisineType));
    }

    @GetMapping("/my")
    public ResponseEntity<List<RestaurantResponse>> getMyRestaurants(
            @RequestHeader("X-User-ID") Long ownerId) {
        return ResponseEntity.ok(restaurantService.getMyRestaurant(ownerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @PathVariable Long id) {
        return restaurantService.getRestaurantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/toggle-open")
    public ResponseEntity<RestaurantResponse> toggleOpen(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long ownerId) {
        return ResponseEntity.ok(restaurantService.toggleOpen(id, ownerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long ownerId) {
        restaurantService.deleteRestaurant(id, ownerId);
        return ResponseEntity.ok("Restaurant deleted");
    }

    // ── Menu item endpoints ──────────────────────────────────────────────────

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @PathVariable Long restaurantId,
            @RequestBody MenuItemRequest request,
            @RequestHeader("X-User-ID") Long ownerId) {
        return new ResponseEntity<>(
                restaurantService.addMenuItem(restaurantId, request, ownerId),
                HttpStatus.CREATED);
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemResponse>> getMenu(
            @PathVariable Long restaurantId) {
        System.out.println(restaurantId);
        return ResponseEntity.ok(restaurantService.getMenu(restaurantId));
    }

    @GetMapping("/{restaurantId}/menu/category/{category}")
    public ResponseEntity<List<MenuItemResponse>> getMenuByCategory(
            @PathVariable Long restaurantId,
            @PathVariable String category) {
        return ResponseEntity.ok(
                restaurantService.getMenuByCategory(restaurantId, category));
    }

    @PatchMapping("/menu/{itemId}/toggle-availability")
    public ResponseEntity<MenuItemResponse> toggleAvailability(
            @PathVariable Long itemId,
            @RequestHeader("X-User-ID") Long ownerId) {
        return ResponseEntity.ok(
                restaurantService.toggleMenuItemAvailability(itemId, ownerId));
    }

    // Internal endpoint — called by order-service via Feign
    @GetMapping("/internal/menu/{itemId}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(
            @PathVariable Long itemId) {
        return restaurantService.getMenuItemById(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}