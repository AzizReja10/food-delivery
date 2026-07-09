package com.fooddelivery.delivery.repository;

import com.fooddelivery.delivery.entity.Delivery;
import com.fooddelivery.delivery.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepo extends JpaRepository<Delivery,Long> {
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByPartnerId(Long partnerId);
    List<Delivery> findByCustomerId(Long customerId);
    List<Delivery> findByStatus(DeliveryStatus status);
}
