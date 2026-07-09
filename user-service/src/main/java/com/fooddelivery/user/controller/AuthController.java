package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.AuthResponse;
import com.fooddelivery.user.dto.LoginRequest;
import com.fooddelivery.user.dto.RegisterRequest;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register/customer")
    public ResponseEntity<String> registerCustomer(@RequestBody RegisterRequest request)
    {
        return new ResponseEntity<>(userService.register(request, UserRole.CUSTOMER), HttpStatus.OK);
    }
    @PostMapping("/register/delivery-partner")
    public ResponseEntity<String> registerDeliveryPartner(@RequestBody RegisterRequest request)
    {
        return new ResponseEntity<>(userService.register(request,UserRole.DELIVERY_PARTNER),HttpStatus.OK);
    }
    @PostMapping("/register/restaurant-owner")
    public ResponseEntity<String> registerRestaurantOwner(@RequestBody RegisterRequest request)
    {
        return new ResponseEntity<>(userService.register(request,UserRole.RESTAURANT_OWNER),HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(userService.login(request));
    }
}
