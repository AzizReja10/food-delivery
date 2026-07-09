package com.fooddelivery.order.service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.fooddelivery.order.client.RestaurantClient;
import com.fooddelivery.order.dto.*;
import com.fooddelivery.order.entity.Order;
import com.fooddelivery.order.entity.OrderItem;
import com.fooddelivery.order.entity.OrderStatus;
import com.fooddelivery.order.repository.OrderRepo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final RestaurantClient restaurantClient;
    private final KafkaTemplate<String , OrderPlacedEvent> kafkaTemplate;

    public OrderService(OrderRepo orderRepo, RestaurantClient restaurantClient, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.orderRepo = orderRepo;
        this.restaurantClient = restaurantClient;
        this.kafkaTemplate = kafkaTemplate;
    }
    public OrderResponse placeOrder(Long customerId, OrderRequest request)
    {
        RestaurantDTO restaurant=restaurantClient.getRestaurantById(request.getRestaurantId());
        if(!restaurant.getIsOpen())
            throw new RuntimeException("Restaurant is closed");
        Order order=new Order();
        order.setCustomerId(customerId);
        order.setRestaurantId(request.getRestaurantId());
        order.setRestaurantName(restaurant.getName());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setStatus(OrderStatus.PENDING);
        List<OrderItem> orderItemList=request.getItems().stream().map(itemReq->{
            MenuItemDTO menuItemDTO=restaurantClient.getMenuItemById(itemReq.getMenuItemId());
            if(!menuItemDTO.getIsAvailable())
            {
                throw new RuntimeException(menuItemDTO.getName()+" is not available");
            }
            OrderItem item=new OrderItem();
            item.setMenuItemId(itemReq.getMenuItemId());
            item.setItemName(menuItemDTO.getName());
            item.setUnitPrice(menuItemDTO.getPrice());
            item.setQuantity(itemReq.getQuantity());
            item.setLineTotal(menuItemDTO.getPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            item.setOrder(order);
            return item;
        }).collect(Collectors.toList());
        BigDecimal total=orderItemList.stream().map(OrderItem::getLineTotal).reduce(BigDecimal.ZERO,BigDecimal::add);
        order.setItems(orderItemList);
        order.setTotalAmount(total);
        Order saved=orderRepo.save(order);
        OrderPlacedEvent event=new OrderPlacedEvent(
                saved.getId(),
                saved.getCustomerId(),
                saved.getRestaurantId(),
                saved.getRestaurantName(),
                saved.getTotalAmount(),
                saved.getDeliveryAddress(),
                saved.getCreatedAt()
        );
        kafkaTemplate.send("order.placed", event);
        return mapToResponse(saved);
    }
    public List<OrderResponse> getMyOrders(Long customerId)
    {
        return orderRepo.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public Optional<OrderResponse> getOrderById(Long orderId)
    {
        return orderRepo.findById(orderId).map(this::mapToResponse);
    }
    public OrderResponse updateStatus(Long orderId,OrderStatus status)
    {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return mapToResponse(orderRepo.save(order));
    }
    public OrderResponse cancelOrder(Long orderId,Long customerId)
    {
        Order order=orderRepo.findById(orderId).orElseThrow(()->new RuntimeException("Order not found"));
        if(!order.getCustomerId().equals(customerId))
        {
            throw new RuntimeException("Unauthorize");
        }
        if(order.getStatus()!=OrderStatus.PENDING)
        {
            throw new RuntimeException( "Cannot cancel order in " + order.getStatus() + " status");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return mapToResponse(orderRepo.save(order));
    }
    private OrderResponse mapToResponse(Order order)
    {
        OrderResponse response=new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setRestaurantId(order.getRestaurantId());
        response.setRestaurantName(order.getRestaurantName());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setSpecialInstructions(order.getSpecialInstructions());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(
                order.getItems().stream().map(item->{
                    OrderItemResponse itemResponse=new OrderItemResponse();
                    itemResponse.setId(item.getId());
                    itemResponse.setMenuItemId(item.getMenuItemId());
                    itemResponse.setItemName(item.getItemName());
                    itemResponse.setUnitPrice(item.getUnitPrice());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setLineTotal(item.getLineTotal());
                    return itemResponse;
                }).collect(Collectors.toList()));
                return  response;
    }
}
