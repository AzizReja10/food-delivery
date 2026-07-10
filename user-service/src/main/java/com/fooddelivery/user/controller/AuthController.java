package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.AuthResponse;
import com.fooddelivery.user.dto.LoginRequest;
import com.fooddelivery.user.dto.RegisterRequest;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log =
            LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register/customer")
    public ResponseEntity<String> registerCustomer(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register customer request: email={}", request.getEmail());
        String result = userService.register(request, UserRole.CUSTOMER);
        log.info("Customer registered successfully: email={}", request.getEmail());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/register/delivery-partner")
    public ResponseEntity<String> registerDeliveryPartner(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register delivery partner request: email={}", request.getEmail());
        String result = userService.register(request, UserRole.DELIVERY_PARTNER);
        log.info("Delivery partner registered: email={}", request.getEmail());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/register/restaurant-owner")
    public ResponseEntity<String> registerRestaurantOwner(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Register restaurant owner request: email={}", request.getEmail());
        String result = userService.register(request, UserRole.RESTAURANT_OWNER);
        log.info("Restaurant owner registered: email={}", request.getEmail());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("Login request: email={}", request.getEmail());
        AuthResponse response = userService.login(request);
        log.info("Login successful: email={}, role={}",
                request.getEmail(), response.getRole());
        return ResponseEntity.ok(response);
    }
}