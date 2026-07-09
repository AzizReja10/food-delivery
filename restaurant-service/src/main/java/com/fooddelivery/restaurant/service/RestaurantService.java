package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.MenuItemRequest;
import com.fooddelivery.restaurant.dto.MenuItemResponse;
import com.fooddelivery.restaurant.dto.RestaurantRequest;
import com.fooddelivery.restaurant.dto.RestaurantResponse;
import com.fooddelivery.restaurant.entity.*;
import com.fooddelivery.restaurant.repository.MenuItemRepo;
import com.fooddelivery.restaurant.repository.RestaurantRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {
    public RestaurantService(RestaurantRepo restaurantRepo, MenuItemRepo menuItemRepo) {
        this.restaurantRepo = restaurantRepo;
        this.menuItemRepo = menuItemRepo;
    }
    private final RestaurantRepo restaurantRepo;
    private final MenuItemRepo menuItemRepo;
    public RestaurantResponse createRestaurant(RestaurantRequest request, Long ownerId)
    {
        Restaurant restaurant=new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setCity(request.getCity());
        restaurant.setPhone(request.getPhone());
        restaurant.setImageUrl(request.getImageUrl());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setOwnerId(ownerId);
        return mapToRestaurantResponse(restaurantRepo.save(restaurant));
    }
    public List<RestaurantResponse> getAllRestaurant()
    {
        return restaurantRepo.findByActiveTrue().stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }
    public List<RestaurantResponse> getOpenRestaurant()
    {
        return restaurantRepo.findByIsOpenTrueAndActiveTrue()
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }
    public List<RestaurantResponse> getRestaurantByCity(String city)
    {
        return restaurantRepo.findByCityAndActiveTrue(city)
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }
    public List<RestaurantResponse> getRestaurantByCuisine(CuisineType cuisineType)
    {
        return restaurantRepo.findByCuisineTypeAndActiveTrue(cuisineType)
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }
    public List<RestaurantResponse> getMyRestaurant(Long ownerId)
    {
        return restaurantRepo.findByOwnerIdAndActiveTrue(ownerId)
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }
    public RestaurantResponse toggleOpen(Long id,Long ownerId)
    {
        Restaurant restaurant=restaurantRepo.findById(id).orElseThrow(()-> new RuntimeException("Restaurant not found"));
        if(!restaurant.getOwnerId().equals(ownerId))
            throw new RuntimeException("Unauthorized");
        restaurant.setIsOpen(!restaurant.getIsOpen());
        return mapToRestaurantResponse(restaurantRepo.save(restaurant));
    }
    public Optional<RestaurantResponse> getRestaurantById(Long id) {
        return restaurantRepo.findById(id).map(this::mapToRestaurantResponse);
    }
    public boolean deleteRestaurant(Long id,Long ownerId)
    {
        Restaurant restaurant = restaurantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        if (!restaurant.getOwnerId().equals(ownerId)) {
            return false;
        }
        restaurant.setActive(false);
        restaurantRepo.save(restaurant);
        return true;
    }
    private RestaurantResponse mapToRestaurantResponse(Restaurant r) {
        RestaurantResponse response=new RestaurantResponse();
        response.setId(r.getId());
        response.setName(r.getName());
        response.setOwnerId(Long.valueOf(r.getOwnerId()));
        response.setAddress(r.getAddress());
        response.setCity(r.getCity());
        response.setPhone(r.getPhone());
        response.setImageUrl(r.getImageUrl());
        response.setCuisineType(r.getCuisineType());
        response.setIsOpen(r.getIsOpen());
        response.setRating(r.getRating());
        return response;
    }
// ── Menu Items ───────────────────────────────────────────────────────────

    public MenuItemResponse addMenuItem(Long restaurantId,
                                        MenuItemRequest request,
                                        Long ownerId) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        item.setImageUrl(request.getImageUrl());
        item.setIsVeg(request.getIsVeg());
        return mapToMenuItemResponse(menuItemRepo.save(item));
    }

    public List<MenuItemResponse> getMenu(Long restaurantId) {
        return menuItemRepo.findByRestaurantIdAndIsAvailableTrue(restaurantId).stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    public List<MenuItemResponse> getMenuByCategory(Long restaurantId, String category) {
        return menuItemRepo.findByRestaurantIdAndCategory(restaurantId, category).stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    public MenuItemResponse toggleMenuItemAvailability(Long itemId, Long ownerId) {
        MenuItem item = menuItemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        if (!item.getRestaurant().getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Unauthorized");
        }
        item.setIsAvailable(!item.getIsAvailable());
        return mapToMenuItemResponse(menuItemRepo.save(item));
    }

    // Internal endpoint — called by order-service via Feign
    public Optional<MenuItemResponse> getMenuItemById(Long id) {
        return menuItemRepo.findById(id).map(this::mapToMenuItemResponse);
    }
    private MenuItemResponse mapToMenuItemResponse(MenuItem item) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(item.getId());
        response.setRestaurantId(item.getRestaurant().getId());
        response.setRestaurantName(item.getRestaurant().getName());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());
        response.setCategory(item.getCategory());
        response.setImageUrl(item.getImageUrl());
        response.setIsAvailable(item.getIsAvailable());
        response.setIsVeg(item.getIsVeg());
        return response;
    }
}
