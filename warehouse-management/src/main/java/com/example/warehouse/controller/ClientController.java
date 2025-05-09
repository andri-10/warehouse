package com.example.warehouse.controller;

import com.example.warehouse.entity.Order;
import com.example.warehouse.entity.OrderItem;
import com.example.warehouse.entity.OrderStatus;
import com.example.warehouse.entity.User;
import com.example.warehouse.service.OrderService;
import com.example.warehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public ClientController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserByUsername(authentication.getName());
    }

    // Create a new order
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        order.setClient(getCurrentUser());
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    // Get all orders for current client
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getMyOrders() {
        return ResponseEntity.ok(orderService.getOrdersByClient(getCurrentUser()));
    }

    // Add item to order
    @PostMapping("/orders/{orderId}/items")
    public ResponseEntity<Order> addItemToOrder(@PathVariable Long orderId, @RequestBody OrderItem orderItem) {
        return ResponseEntity.ok(orderService.addItemToOrder(orderId, orderItem));
    }

    // Submit order
    @PostMapping("/orders/{orderId}/submit")
    public ResponseEntity<Order> submitOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.submitOrder(orderId));
    }

    // Cancel order
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("Authentication successful");
    }
}