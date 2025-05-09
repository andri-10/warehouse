package com.example.warehouse.service.impl;

import com.example.warehouse.entity.Truck;
import com.example.warehouse.repository.DeliveryRepository;
import com.example.warehouse.repository.TruckRepository;
import com.example.warehouse.service.TruckService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TruckServiceImpl implements TruckService {

    private final TruckRepository truckRepository;
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public TruckServiceImpl(TruckRepository truckRepository, DeliveryRepository deliveryRepository) {
        this.truckRepository = truckRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    @Transactional
    public Truck createTruck(Truck truck) {
        if(truckRepository.existsByLicensePlate(truck.getLicensePlate())) {
            throw new IllegalArgumentException("Truck with this license plate already exists");
        }
        return truckRepository.save(truck);
    }

    @Override
    @Transactional
    public Truck updateTruck(Truck truck) {
        Truck existingTruck = truckRepository.findById(truck.getId())
                .orElseThrow(() -> new IllegalArgumentException("Truck with this id does not exist"));

        if(!existingTruck.getLicensePlate().equals(truck.getLicensePlate()) &&
            truckRepository.existsByLicensePlate(truck.getLicensePlate())){
            throw new IllegalArgumentException("Truck with this license plate already exists");
        }
        return truckRepository.save(existingTruck);
    }

    @Override
    @Transactional
    public void deleteTruck(Long truckId) {
        if(!truckRepository.existsById(truckId)) {
            throw new IllegalArgumentException("Truck not found");
        }
        truckRepository.deleteById(truckId);
    }

    @Override
    public Truck getTruckById(Long truckId) {
        return truckRepository.findById(truckId)
                .orElseThrow(() -> new IllegalArgumentException("Truck not found"));
    }

    @Override
    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    @Override
    public Truck getTruckByLicensePlate(String licensePlate) {
        return truckRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new IllegalArgumentException("Truck not found"));
    }

    @Override
    public boolean existsByLicensePlate(String licensePlate) {
        return truckRepository.existsByLicensePlate(licensePlate);
    }

    @Override
    public List<Truck> getAvailableTrucksForDate(LocalDate date) {
        List<Truck> trucks = getAllTrucks();
        return trucks.stream()
                .filter(truck -> deliveryRepository.findByTruckAndDeliveryDate(truck, date).isEmpty())
                .collect(Collectors.toList());
    }
}
