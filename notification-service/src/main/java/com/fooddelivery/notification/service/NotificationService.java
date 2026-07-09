package com.fooddelivery.notification.service;

import com.fooddelivery.notification.dto.OrderDeliveredEvent;
import com.fooddelivery.notification.dto.OrderPlacedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @KafkaListener(
            topics = "order.placed",
            groupId = "notification-service-group",
            containerFactory = "orderPlacedKafkaListenerContainerFactory")
    public void handleOrderPlaced(@Payload OrderPlacedEvent event) {
        System.out.println("=== NOTIFICATION ===");
        System.out.println("New order placed!");
        System.out.println("Order ID    : " + event.getOrderId());
        System.out.println("Customer ID : " + event.getCustomerId());
        System.out.println("Restaurant  : " + event.getRestaurantName());
        System.out.println("Total Amount: ₹" + event.getTotalAmount());
        System.out.println("Address     : " + event.getDeliveryAddress());
        System.out.println("SMS to customer " + event.getCustomerId()
                + ": Your order #" + event.getOrderId()
                + " has been placed at " + event.getRestaurantName()
                + ". Total: ₹" + event.getTotalAmount());
        System.out.println("====================");
    }

    @KafkaListener(
            topics = "order.delivered",
            groupId = "notification-service-group",
            containerFactory = "orderDeliveredKafkaListenerContainerFactory")
    public void handleOrderDelivered(@Payload OrderDeliveredEvent event) {
        System.out.println("=== NOTIFICATION ===");
        System.out.println("Order delivered!");
        System.out.println("Order ID    : " + event.getOrderId());
        System.out.println("Customer ID : " + event.getCustomerId());
        System.out.println("Partner ID  : " + event.getPartnerId());
        System.out.println("Delivered At: " + event.getDeliveredAt());
        System.out.println("SMS to customer " + event.getCustomerId()
                + ": Your order #" + event.getOrderId()
                + " has been delivered! Enjoy your meal!");
        System.out.println("====================");
    }
}