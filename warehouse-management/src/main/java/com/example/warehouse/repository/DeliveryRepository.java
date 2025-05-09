package com.example.warehouse.repository;

import com.example.warehouse.entity.Delivery;
import com.example.warehouse.entity.Order;
import com.example.warehouse.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByDeliveryDate(LocalDate deliveryDate);

    List<Delivery> findByDeliveryDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByOrderAndCompletedFalse(Order order);

    @Query("SELECT d FROM Delivery d JOIN d.trucks t WHERE t = :truck AND d.deliveryDate = :date")
    List<Delivery> findByTruckAndDeliveryDate(@Param("truck") Truck truck, @Param("date") LocalDate date);

    List<Delivery> findByCompletedFalseAndDeliveryDateLessThanEqual(LocalDate date);
}