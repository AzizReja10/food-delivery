package com.fooddelivery.delivery.controller;

import com.fooddelivery.delivery.DTO.DeliveryPartnerResponse;
import com.fooddelivery.delivery.DTO.DeliveryResponse;
import com.fooddelivery.delivery.DTO.RegisterPartnerRequest;
import com.fooddelivery.delivery.entity.DeliveryStatus;
import com.fooddelivery.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private static final Logger log =
            LoggerFactory.getLogger(DeliveryController.class);

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/partners/register")
    public ResponseEntity<DeliveryPartnerResponse> registerPartner(
            @Valid @RequestBody RegisterPartnerRequest request,
            @RequestHeader("X-User-ID") Long userId) {
        log.info("Register delivery partner: userId={}", userId);
        DeliveryPartnerResponse response =
                deliveryService.registerPartner(userId, request);
        log.info("Delivery partner registered: partnerId={}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/partners/available")
    public ResponseEntity<List<DeliveryPartnerResponse>> getAvailablePartners() {
        log.info("Get available delivery partners");
        return ResponseEntity.ok(deliveryService.getAvailablePartners());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getDeliveryByOrderId(
            @PathVariable Long orderId) {
        log.info("Get delivery by orderId: {}", orderId);
        return deliveryService.getDeliveryByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    public ResponseEntity<List<DeliveryResponse>> getMyDeliveries(
            @RequestHeader("X-User-ID") Long userId) {
        log.info("Get my deliveries: userId={}", userId);
        return ResponseEntity.ok(deliveryService.getMyDeliveries(userId));
    }

    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status,
            @RequestHeader("X-User-ID") Long userId) {
        log.info("Update delivery status: deliveryId={}, status={}, userId={}",
                deliveryId, status, userId);
        DeliveryResponse response =
                deliveryService.updateStatus(deliveryId, status, userId);
        log.info("Delivery status updated: deliveryId={}, status={}",
                deliveryId, status);
        return ResponseEntity.ok(response);
    }
}