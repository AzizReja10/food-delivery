package com.fooddelivery.user.dto;

import lombok.Data;

@Data
public class AddressRequest {
    private String city;
    private String state;
    private String street;
    private String zipcode;
    private Boolean isDefault=false;
}
