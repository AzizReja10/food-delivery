package com.fooddelivery.user.controller;

import com.fooddelivery.user.dto.AddressRequest;
import com.fooddelivery.user.dto.AddressResponse;
import com.fooddelivery.user.dto.UserResponse;
import com.fooddelivery.user.entity.UserRole;
import com.fooddelivery.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    public final UserService userService;
    public UserController(UserService userService)
    {
        this.userService=userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id)
    {
        return userService.getUserById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/admin/allUser")
    public ResponseEntity<List<UserResponse>> getAllUser()
    {
        return ResponseEntity.ok(userService.getUser()) ;
    }
    // Internal endpoint — called by other services via Feign
    @GetMapping("/internal/{id}")
    public ResponseEntity<UserResponse> getUserInternal(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/admin/{id}/role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable Long id, @RequestParam UserRole role)
    {
        return ResponseEntity.ok(userService.updateRole(id, role));
    }
    @PostMapping("/{id}/address")
    public ResponseEntity<AddressResponse> addAddress(@PathVariable Long id, @RequestBody AddressRequest request)
    {
        return ResponseEntity.ok(userService.addAddress(id,request));
    }
    @GetMapping("/{id}/address")
    public ResponseEntity<List<AddressResponse>> getAddress(@PathVariable Long id)
    {
        return ResponseEntity.ok(userService.getAddress(id));
    }
}
