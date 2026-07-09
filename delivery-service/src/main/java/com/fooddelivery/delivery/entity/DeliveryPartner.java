package com.fooddelivery.delivery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "delivery_partner")
@NoArgsConstructor
public class DeliveryPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private Long userId;
    private String name;
    private String phone;
    private Boolean isAvailable = true;
    private Integer totalDeliveries = 0;
}
