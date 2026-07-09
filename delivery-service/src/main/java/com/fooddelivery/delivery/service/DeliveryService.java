package com.fooddelivery.delivery.service;

import com.fooddelivery.delivery.DTO.*;
import com.fooddelivery.delivery.entity.Delivery;
import com.fooddelivery.delivery.entity.DeliveryPartner;
import com.fooddelivery.delivery.entity.DeliveryStatus;
import com.fooddelivery.delivery.repository.DeliveryPartnerRepo;
import com.fooddelivery.delivery.repository.DeliveryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepo deliveryRepo;

    @Autowired
    private DeliveryPartnerRepo partnerRepo;

    @Autowired
    private KafkaTemplate<String, OrderDeliveredEvent> kafkaTemplate;

    // ── Kafka Consumer ───────────────────────────────────────────────────────

    @KafkaListener(topics = "order.placed", groupId = "delivery-service-group")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        System.out.println("Received order.placed event for order: "
                + event.getOrderId());

        // Find an available delivery partner
        List<DeliveryPartner> available = partnerRepo.findByIsAvailableTrue();

        Delivery delivery = new Delivery();
        delivery.setOrderId(event.getOrderId());
        delivery.setCustomerId(event.getCustomerId());
        delivery.setRestaurantId(event.getRestaurantId());
        delivery.setRestaurantName(event.getRestaurantName());
        delivery.setDeliveryAddress(event.getDeliveryAddress());
        delivery.setAssignedAt(LocalDateTime.now());

        if (!available.isEmpty()) {
            // Assign first available partner
            DeliveryPartner partner = available.get(0);
            delivery.setPartnerId(partner.getId());
            delivery.setStatus(DeliveryStatus.ASSIGNED);

            // Mark partner as unavailable
            partner.setIsAvailable(false);
            partnerRepo.save(partner);

            System.out.println("Assigned delivery partner: "
                    + partner.getName() + " for order: " + event.getOrderId());
        } else {
            delivery.setStatus(DeliveryStatus.ASSIGNED);
            System.out.println("No available partners for order: "
                    + event.getOrderId() + " — will assign later");
        }

        deliveryRepo.save(delivery);
    }

    // ── Delivery partner management ──────────────────────────────────────────

    public DeliveryPartnerResponse registerPartner(Long userId,
                                                   RegisterPartnerRequest request) {
        DeliveryPartner partner = new DeliveryPartner();
        partner.setUserId(userId);
        partner.setName(request.getName());
        partner.setPhone(request.getPhone());
        return mapToPartnerResponse(partnerRepo.save(partner));
    }

    public List<DeliveryPartnerResponse> getAvailablePartners() {
        return partnerRepo.findByIsAvailableTrue().stream()
                .map(this::mapToPartnerResponse)
                .collect(Collectors.toList());
    }

    // ── Delivery status updates ──────────────────────────────────────────────

    public DeliveryResponse updateStatus(Long deliveryId,
                                         DeliveryStatus status,
                                         Long userId) {
        Delivery delivery = deliveryRepo.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        // Find partner by userId (X-User-ID from gateway)
        DeliveryPartner partner = partnerRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

        // Check if this partner owns this delivery
        if (!delivery.getPartnerId().equals(partner.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        delivery.setStatus(status);

        if (status == DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(LocalDateTime.now());
        }

        if (status == DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());

            partner.setIsAvailable(true);
            partner.setTotalDeliveries(partner.getTotalDeliveries() + 1);
            partnerRepo.save(partner);

            OrderDeliveredEvent event = new OrderDeliveredEvent(
                    delivery.getOrderId(),
                    delivery.getCustomerId(),
                    partner.getId(),
                    delivery.getDeliveredAt()
            );
            kafkaTemplate.send("order.delivered", event);
            System.out.println("Published order.delivered event for order: "
                    + delivery.getOrderId());
        }

        return mapToDeliveryResponse(deliveryRepo.save(delivery));
    }

    public Optional<DeliveryResponse> getDeliveryByOrderId(Long orderId) {
        return deliveryRepo.findByOrderId(orderId)
                .map(this::mapToDeliveryResponse);
    }

    public List<DeliveryResponse> getMyDeliveries(Long partnerId) {
        return deliveryRepo.findByPartnerId(partnerId).stream()
                .map(this::mapToDeliveryResponse)
                .collect(Collectors.toList());
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private DeliveryResponse mapToDeliveryResponse(Delivery d) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(d.getId());
        response.setOrderId(d.getOrderId());
        response.setPartnerId(d.getPartnerId());
        response.setCustomerId(d.getCustomerId());
        response.setRestaurantId(d.getRestaurantId());
        response.setRestaurantName(d.getRestaurantName());
        response.setDeliveryAddress(d.getDeliveryAddress());
        response.setStatus(d.getStatus());
        response.setAssignedAt(d.getAssignedAt());
        response.setPickedUpAt(d.getPickedUpAt());
        response.setDeliveredAt(d.getDeliveredAt());
        return response;
    }

    private DeliveryPartnerResponse mapToPartnerResponse(DeliveryPartner p) {
        DeliveryPartnerResponse response = new DeliveryPartnerResponse();
        response.setId(p.getId());
        response.setUserId(p.getUserId());
        response.setName(p.getName());
        response.setPhone(p.getPhone());
        response.setIsAvailable(p.getIsAvailable());
        response.setTotalDeliveries(p.getTotalDeliveries());
        return response;
    }
}