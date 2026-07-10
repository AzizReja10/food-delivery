package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.AddressRequest;
import com.fooddelivery.user.dto.AddressResponse;
import com.fooddelivery.user.dto.UserResponse;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log =
            LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        log.info("Get user request: id={}", id);
        return userService.getUserById(id)
                .map(user -> {
                    log.info("User found: id={}", id);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/internal/{id}")
    public ResponseEntity<UserResponse> getUserInternal(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @RequestParam UserRole role) {
        log.info("Update role request: userId={}, newRole={}", id, role);
        UserResponse response = userService.updateRole(id, role);
        log.info("Role updated: userId={}, role={}", id, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        log.info("Add address request: userId={}", id);
        return ResponseEntity.ok(userService.addAddress(id, request));
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(
            @PathVariable Long id) {
        log.info("Get addresses request: userId={}", id);
        return ResponseEntity.ok(userService.getAddress(id));
    }
}