package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.dto.OrderResponse;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log =
            LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader("X-User-ID") Long customerId) {
        log.info("Place order request: customerId={}, restaurantId={}",
                customerId, request.getRestaurantId());
        OrderResponse response = orderService.placeOrder(customerId, request);
        log.info("Order placed: orderId={}, customerId={}",
                response.getId(), customerId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @RequestHeader("X-User-ID") Long customerId) {
        log.info("Get my orders: customerId={}", customerId);
        return ResponseEntity.ok(orderService.getMyOrders(customerId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId) {
        log.info("Get order by id: {}", orderId);
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        log.info("Update order status: orderId={}, status={}", orderId, status);
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("X-User-ID") Long customerId) {
        log.info("Cancel order: orderId={}, customerId={}", orderId, customerId);
        OrderResponse response = orderService.cancelOrder(orderId, customerId);
        log.info("Order cancelled: orderId={}", orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/internal/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByRestaurant(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(orderService.getMyOrders(restaurantId));
    }
}