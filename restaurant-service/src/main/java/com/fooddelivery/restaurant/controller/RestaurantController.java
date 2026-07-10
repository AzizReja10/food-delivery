package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.dto.*;
import com.fooddelivery.restaurant.entity.CuisineType;
import com.fooddelivery.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private static final Logger log =
            LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantResponse> createRestaurant(
            @Valid @RequestBody RestaurantRequest request,
            @RequestHeader("X-User-ID") Long ownerId) {
        log.info("Create restaurant request: name={}, ownerId={}",
                request.getName(), ownerId);
        RestaurantResponse response =
                restaurantService.createRestaurant(request, ownerId);
        log.info("Restaurant created: id={}, name={}",
                response.getId(), response.getName());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        log.info("Get all restaurants request");
        return ResponseEntity.ok(restaurantService.getAllRestaurant());
    }

    @GetMapping("/open")
    public ResponseEntity<List<RestaurantResponse>> getOpenRestaurants() {
        log.info("Get open restaurants request");
        return ResponseEntity.ok(restaurantService.getOpenRestaurant());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<RestaurantResponse>> getByCity(
            @PathVariable String city) {
        log.info("Get restaurants by city: {}", city);
        return ResponseEntity.ok(restaurantService.getRestaurantByCity(city));
    }

    @GetMapping("/cuisine/{cuisineType}")
    public ResponseEntity<List<RestaurantResponse>> getByCuisine(
            @PathVariable CuisineType cuisineType) {
        log.info("Get restaurants by cuisine: {}", cuisineType);
        return ResponseEntity.ok(
                restaurantService.getRestaurantByCuisine(cuisineType));
    }

    @GetMapping("/my")
    public ResponseEntity<List<RestaurantResponse>> getMyRestaurants(
            @RequestHeader("X-User-ID") Long ownerId) {
        log.info("Get my restaurants: ownerId={}", ownerId);
        return ResponseEntity.ok(restaurantService.getMyRestaurant(ownerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(
            @PathVariable Long id) {
        log.info("Get restaurant by id: {}", id);
        return restaurantService.getRestaurantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/toggle-open")
    public ResponseEntity<RestaurantResponse> toggleOpen(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long ownerId) {
        log.info("Toggle restaurant open: id={}, ownerId={}", id, ownerId);
        return ResponseEntity.ok(restaurantService.toggleOpen(id, ownerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(
            @PathVariable Long id,
            @RequestHeader("X-User-ID") Long ownerId) {
        log.info("Delete restaurant: id={}, ownerId={}", id, ownerId);
        restaurantService.deleteRestaurant(id, ownerId);
        return ResponseEntity.ok("Restaurant deleted");
    }

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @PathVariable Long restaurantId,
            @Valid @RequestBody MenuItemRequest request,
            @RequestHeader("X-User-ID") Long ownerId) {
        log.info("Add menu item: restaurantId={}, item={}, ownerId={}",
                restaurantId, request.getName(), ownerId);
        return new ResponseEntity<>(
                restaurantService.addMenuItem(restaurantId, request, ownerId),
                HttpStatus.CREATED);
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItemResponse>> getMenu(
            @PathVariable Long restaurantId) {
        log.info("Get menu: restaurantId={}", restaurantId);
        return ResponseEntity.ok(restaurantService.getMenu(restaurantId));
    }

    @GetMapping("/{restaurantId}/menu/category/{category}")
    public ResponseEntity<List<MenuItemResponse>> getMenuByCategory(
            @PathVariable Long restaurantId,
            @PathVariable String category) {
        log.info("Get menu by category: restaurantId={}, category={}",
                restaurantId, category);
        return ResponseEntity.ok(
                restaurantService.getMenuByCategory(restaurantId, category));
    }

    @PatchMapping("/menu/{itemId}/toggle-availability")
    public ResponseEntity<MenuItemResponse> toggleAvailability(
            @PathVariable Long itemId,
            @RequestHeader("X-User-ID") Long ownerId) {
        log.info("Toggle menu item availability: itemId={}, ownerId={}",
                itemId, ownerId);
        return ResponseEntity.ok(
                restaurantService.toggleMenuItemAvailability(itemId, ownerId));
    }

    @GetMapping("/internal/menu/{itemId}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(
            @PathVariable Long itemId) {
        return restaurantService.getMenuItemById(itemId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}