package com.example.warehouse.controller;

import com.example.warehouse.entity.*;
import com.example.warehouse.service.ItemService;
import com.example.warehouse.service.OrderService;
import com.example.warehouse.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final OrderService orderService;
    private final ItemService itemService;
    private final TruckService truckService;

    @Autowired
    public ManagerController(OrderService orderService, ItemService itemService, TruckService truckService) {
        this.orderService = orderService;
        this.itemService = itemService;
        this.truckService = truckService;
    }

    // View all orders
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // Approve order
    @PostMapping("/orders/{orderId}/approve")
    public ResponseEntity<Order> approveOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.approveOrder(orderId));
    }

    // Decline order
    @PostMapping("/orders/{orderId}/decline")
    public ResponseEntity<Order> declineOrder(@PathVariable Long orderId, @RequestParam String reason) {
        return ResponseEntity.ok(orderService.declineOrder(orderId, reason));
    }

    // Schedule delivery
    @PostMapping("/orders/{orderId}/schedule-delivery")
    public ResponseEntity<Order> scheduleDelivery(
            @PathVariable Long orderId,
            @RequestParam LocalDate deliveryDate,
            @RequestBody List<Long> truckIds) {
        return ResponseEntity.ok(orderService.scheduleDelivery(orderId, deliveryDate, truckIds));
    }

    // Create new item
    @PostMapping("/items")
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        return ResponseEntity.ok(itemService.createItem(item));
    }

    // Get all trucks
    @GetMapping("/trucks")
    public ResponseEntity<List<Truck>> getAllTrucks() {
        return ResponseEntity.ok(truckService.getAllTrucks());
    }
}