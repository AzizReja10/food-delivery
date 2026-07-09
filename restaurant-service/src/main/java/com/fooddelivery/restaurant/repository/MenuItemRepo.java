package com.fooddelivery.restaurant.repository;

import com.fooddelivery.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem,Long> {
    List<MenuItem> findByRestaurantId(Long id);
    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(Long id);
    List<MenuItem> findByRestaurantIdAndCategory(Long id,String category);
}
