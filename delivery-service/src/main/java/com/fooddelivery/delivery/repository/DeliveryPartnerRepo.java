package com.fooddelivery.delivery.repository;

import com.fooddelivery.delivery.entity.DeliveryPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPartnerRepo extends JpaRepository<DeliveryPartner,Long>
{
    Optional<DeliveryPartner> findByUserId(Long userId);

    List<DeliveryPartner> findByIsAvailableTrue();
}
