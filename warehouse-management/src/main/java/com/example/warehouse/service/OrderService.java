package com.example.warehouse.service;

import com.example.warehouse.entity.Order;
import com.example.warehouse.entity.OrderItem;
import com.example.warehouse.entity.OrderStatus;
import com.example.warehouse.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order updateOrder(Order order);
    Order addItemToOrder(Long orderId, OrderItem orderItem);
    Order removeItemFromOrder(Long orderId, Long orderItemId);
    Order updateItemQuantity(Long orderId, Long orderItemId, Integer newQuantity);
    Order submitOrder(Long orderId);
    Order approveOrder(Long orderId);
    Order declineOrder(Long orderId, String reason);
    Order cancelOrder(Long orderId);
    Order scheduleDelivery(Long orderId, LocalDate deliveryDate, List<Long> truckIds);
    Order getOrderById(Long orderId);
    Order getOrderByOrderNumber(String orderNumber);
    List<Order> getOrdersByClient(User client);
    List<Order> getOrdersByClientAndStatus(User client, OrderStatus status);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getAllOrders();
    List<LocalDate> getAvailableDeliveryDates(Long orderId, int days);
    void fulfillDueDeliveries();
}