package com.fooddelivery.notification.service;

import com.fooddelivery.notification.dto.OrderDeliveredEvent;
import com.fooddelivery.notification.dto.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationService.class);

    @KafkaListener(
            topics = "order.placed",
            groupId = "notification-service-group",
            containerFactory = "orderPlacedKafkaListenerContainerFactory")
    public void handleOrderPlaced(@Payload OrderPlacedEvent event) {
        log.info("=== ORDER PLACED NOTIFICATION ===");
        log.info("Order ID    : {}", event.getOrderId());
        log.info("Customer ID : {}", event.getCustomerId());
        log.info("Restaurant  : {}", event.getRestaurantName());
        log.info("Total Amount: ₹{}", event.getTotalAmount());
        log.info("Address     : {}", event.getDeliveryAddress());
        log.info("SMS → Customer {}: Your order #{} has been placed at {}. Total: ₹{}",
                event.getCustomerId(), event.getOrderId(),
                event.getRestaurantName(), event.getTotalAmount());
        log.info("=================================");
    }

    @KafkaListener(
            topics = "order.delivered",
            groupId = "notification-service-group",
            containerFactory = "orderDeliveredKafkaListenerContainerFactory")
    public void handleOrderDelivered(@Payload OrderDeliveredEvent event) {
        log.info("=== ORDER DELIVERED NOTIFICATION ===");
        log.info("Order ID    : {}", event.getOrderId());
        log.info("Customer ID : {}", event.getCustomerId());
        log.info("Partner ID  : {}", event.getPartnerId());
        log.info("Delivered At: {}", event.getDeliveredAt());
        log.info("SMS → Customer {}: Your order #{} has been delivered! Enjoy your meal!",
                event.getCustomerId(), event.getOrderId());
        log.info("====================================");
    }
}