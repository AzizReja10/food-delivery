package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.CuisineType;
import com.fooddelivery.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepo extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByActiveTrue();

    List<Restaurant> findByIsOpenTrueAndActiveTrue();

    List<Restaurant> findByCityAndActiveTrue(String city);

    List<Restaurant> findByCuisineTypeAndActiveTrue(CuisineType cuisineType);

    List<Restaurant> findByOwnerIdAndActiveTrue(Long ownerId);
}