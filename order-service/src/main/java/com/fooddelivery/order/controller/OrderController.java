package com.fooddelivery.order.controller;

import com.fooddelivery.order.dto.OrderRequest;
import com.fooddelivery.order.dto.OrderResponse;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request, @RequestHeader("X-User-ID") Long customerId)
    {
        return ResponseEntity.ok(orderService.placeOrder(customerId,request));
    }
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrder(@RequestHeader("X-User-Id")Long customerId)
    {
        return ResponseEntity.ok(orderService.getMyOrders(customerId));
    }
    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long orderId, @RequestParam OrderStatus status)
    {
        return ResponseEntity.ok(orderService.updateStatus(orderId,status));
    }
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId,@RequestHeader("X-User-ID") Long customerId)
    {
        return ResponseEntity.ok(orderService.cancelOrder(orderId,customerId));
    }
    @GetMapping("/internal/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByRestaurant(
            @PathVariable Long restaurantId) {
        return ResponseEntity.ok(orderService.getMyOrders(restaurantId));
    }
}
