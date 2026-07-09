package com.fooddelivery.delivery.controller;

import com.fooddelivery.delivery.DTO.DeliveryPartnerResponse;
import com.fooddelivery.delivery.DTO.DeliveryResponse;
import com.fooddelivery.delivery.DTO.RegisterPartnerRequest;
import com.fooddelivery.delivery.entity.DeliveryStatus;
import com.fooddelivery.delivery.service.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ResolutionException;
import java.util.List;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }
    @PostMapping("/partners/register")
    public ResponseEntity<DeliveryPartnerResponse> registerPartner(@RequestBody RegisterPartnerRequest request,
                                                                   @RequestHeader("X-User-ID")Long userId)
    {
        return new ResponseEntity<>(deliveryService.registerPartner(userId,request), HttpStatus.CREATED);
    }
    @GetMapping("/partners/available")
    public ResponseEntity<List<DeliveryPartnerResponse>> getAvailablePartners()
    {
        return ResponseEntity.ok(deliveryService.getAvailablePartners());
    }
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<DeliveryResponse> getOrderById(@PathVariable Long orderId)
    {
        return deliveryService.getDeliveryByOrderId(orderId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/my")
    public ResponseEntity<List<DeliveryResponse>> getMyDeliveries(
            @RequestHeader("X-User-ID") Long partnerId) {
        return ResponseEntity.ok(
                deliveryService.getMyDeliveries(partnerId));
    }

    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status,
            @RequestHeader("X-User-ID") Long partnerId) {
        return ResponseEntity.ok(
                deliveryService.updateStatus(deliveryId, status, partnerId));
    }
}
