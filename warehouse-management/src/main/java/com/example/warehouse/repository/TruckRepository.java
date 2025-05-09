package com.example.warehouse.repository;

import com.example.warehouse.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
    Optional<Truck> findByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);
}
