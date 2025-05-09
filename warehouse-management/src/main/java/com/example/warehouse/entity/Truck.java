package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trucks")
@Data
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column(name = "container_volume", nullable = false)
    private Double containerVolume;

    @OneToMany
    private List<Delivery> deliveries = new ArrayList<>();

}
