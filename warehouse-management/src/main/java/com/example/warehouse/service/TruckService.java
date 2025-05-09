package com.example.warehouse.service;

import com.example.warehouse.entity.Truck;
import java.time.LocalDate;
import java.util.List;

public interface TruckService {
    Truck createTruck(Truck truck);
    Truck updateTruck(Truck truck);
    void deleteTruck(Long truckId);
    Truck getTruckById(Long truckId);
    List<Truck> getAllTrucks();
    Truck getTruckByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);
    List<Truck> getAvailableTrucksForDate(LocalDate date);
}