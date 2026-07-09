package com.fooddelivery.user.dto;

import com.fooddelivery.user.entity.UserRole;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private UserRole role;
    private Boolean active;
}
