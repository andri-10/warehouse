package com.example.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "deliveries")
@Data
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToMany
    @JoinTable(
        name = "delivery_trucks",
        joinColumns = @JoinColumn(name = "delivery_id"),
        inverseJoinColumns = @JoinColumn(name = "truck_id")
    )
    private List<Truck> trucks;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    public void addTruck(Truck truck) {
        trucks.add(truck);
    }

    public void removeTruck(Truck truck){
        trucks.remove(truck);
    }



}
