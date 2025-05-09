package com.example.warehouse.service.impl;

import com.example.warehouse.entity.*;
import com.example.warehouse.repository.DeliveryRepository;
import com.example.warehouse.repository.OrderRepository;
import com.example.warehouse.service.ItemService;
import com.example.warehouse.service.OrderService;
import com.example.warehouse.service.TruckService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final ItemService itemService;
    private final TruckService truckService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, DeliveryRepository deliveryRepository, ItemService itemService, TruckService truckService) {
        this.orderRepository = orderRepository;
        this.deliveryRepository = deliveryRepository;
        this.itemService = itemService;
        this.truckService = truckService;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.CREATED);
        return getOrder(order);
    }

    // --- helper methods ---
    private Order getOrder(Order order) {
        if(order.getItems() != null) {
            for (OrderItem orderItem : order.getItems()) {
                Item item = itemService.getItemById(orderItem.getItem().getId());
                orderItem.setItem(item);
                orderItem.setOrder(order);
            }
        }
        return orderRepository.save(order);
    }

    private Order createAndCheckOrder(Long orderId){
        Order order = getOrderById(orderId);

        if(order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.DECLINED) {
            throw new IllegalStateException("Cannot modify order: only orders with status CREATED or DECLINED can be modified.");
        }

        return order;
    }

    @Override
    @Transactional
    public Order updateOrder(Order order) {
        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        if(existingOrder.getStatus() != OrderStatus.CREATED &&
                existingOrder.getStatus() != OrderStatus.DECLINED) {
            throw new IllegalStateException("Cannot update order: only orders with status CREATED or DECLINED can be modified.");
        }

        order.setStatus(existingOrder.getStatus());
        order.setOrderNumber(existingOrder.getOrderNumber());
        order.setClient(existingOrder.getClient());

        return getOrder(order);
    }

    @Override
    @Transactional
    public Order addItemToOrder(Long orderId, OrderItem orderItem) {
        Order order = createAndCheckOrder(orderId);

        Item item = itemService.getItemById(orderItem.getItem().getId());
        orderItem.setItem(item);
        orderItem.setOrder(order);

        order.getItems().add(orderItem);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order removeItemFromOrder(Long orderId, Long orderItemId) {
        Order order = createAndCheckOrder(orderId);

        OrderItem itemToRemove = order.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Order item not found."));

        order.getItems().remove(itemToRemove);
        itemToRemove.setOrder(null);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateItemQuantity(Long orderId, Long orderItemId, Integer newQuantity) {
        if(newQuantity <= 0){
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Order order = createAndCheckOrder(orderId);

        OrderItem itemToUpdate = order.getItems().stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Order item not found."));

        itemToUpdate.setRequestedQuantity(newQuantity);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order submitOrder(Long orderId) {
        Order order = createAndCheckOrder(orderId);

        if(order.getItems() == null || order.getItems().isEmpty()){
            throw new IllegalStateException("Cannot submit an empty order.");
        }

        order.setStatus(OrderStatus.AWAITING_APPROVAL);
        order.setSubmittedDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order approveOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if(order.getStatus() != OrderStatus.AWAITING_APPROVAL){
            throw new IllegalStateException("Cannot approve order: only orders with status AWAITING_APPROVAL can be approved.");
        }

        order.setStatus(OrderStatus.APPROVED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order declineOrder(Long orderId, String reason) {
        Order order = getOrderById(orderId);

        if(order.getStatus() != OrderStatus.AWAITING_APPROVAL){
            throw new IllegalStateException("Cannot decline order: only orders with status AWAITING_APPROVAL can be declined.");
        }

        order.setDeclineReason(reason);
        order.setStatus(OrderStatus.DECLINED);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if(order.getStatus() == OrderStatus.FULFILLED ||
                order.getStatus() == OrderStatus.CANCELLED ||
                order.getStatus() == OrderStatus.UNDER_DELIVERY){
            throw new IllegalStateException("Cannot cancel order: orders with status FULFILLED, CANCELED, or UNDER_DELIVERY cannot be canceled.");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order scheduleDelivery(Long orderId, LocalDate deliveryDate, List<Long> truckIds) {
        Order order = getOrderById(orderId);

        if(order.getStatus() != OrderStatus.APPROVED){
            throw new IllegalStateException("Cannot schedule delivery: order must be APPROVED.");
        }

        if(deliveryDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                deliveryDate.getDayOfWeek() == DayOfWeek.SUNDAY){
            throw new IllegalArgumentException("Cannot schedule delivery on weekend.");
        }

        if(deliveryDate.isBefore(LocalDate.now())){
            throw new IllegalArgumentException("Delivery date must be in the future.");
        }

        List<Truck> trucks = truckIds.stream()
                .map(truckService::getTruckById)
                .collect(Collectors.toList());

        for(Truck truck : trucks){
            if(!deliveryRepository.findByTruckAndDeliveryDate(truck, deliveryDate).isEmpty()){
                throw new IllegalStateException("Truck with license " + truck.getLicensePlate() + " is not available on the requested delivery date.");
            }
        }

        double totalOrderVolume = order.getItems().stream()
                .mapToDouble(item -> item.getItem().getPackageVolume() * item.getRequestedQuantity())
                .sum();

        double totalTruckVolume = trucks.stream()
                .mapToDouble(Truck::getContainerVolume)
                .sum();

        if(totalTruckVolume < totalOrderVolume){
            throw new IllegalArgumentException("Selected trucks do not have enough capacity for this order.");
        }

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryDate(deliveryDate);
        delivery.setCompleted(false);
        delivery.setTrucks(trucks);

        Delivery savedDelivery = deliveryRepository.save(delivery);

        order.setStatus(OrderStatus.UNDER_DELIVERY);
        order.setDelivery(savedDelivery);  // Set the delivery reference in the order

        for(OrderItem orderItem : order.getItems()){
            itemService.updateQuantity(
                    orderItem.getItem().getId(),
                    -orderItem.getRequestedQuantity());
        }

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found."));
    }

    @Override
    public Order getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NoSuchElementException("Order not found."));
    }

    @Override
    public List<Order> getOrdersByClient(User client) {
        return orderRepository.findByClientOrderBySubmittedDateDesc(client);
    }

    @Override
    public List<Order> getOrdersByClientAndStatus(User client, OrderStatus status) {
        return orderRepository.findByClientAndStatusOrderBySubmittedDateDesc(client, status);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderBySubmittedDateDesc(status);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<LocalDate> getAvailableDeliveryDates(Long orderId, int days) {
        int daysToCheck = Math.min(days, 30);

        Order order = getOrderById(orderId);

        if(order.getStatus() != OrderStatus.APPROVED){
            throw new IllegalStateException("Can only get available delivery dates for APPROVED orders.");
        }

        double totalOrderVolume = order.getItems().stream()
                .mapToDouble(item -> item.getItem().getPackageVolume() * item.getRequestedQuantity())
                .sum();

        List<LocalDate> availableDates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < daysToCheck; i++) {
            currentDate = currentDate.plusDays(1);

            if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                continue;
            }

            List<Truck> availableTrucks = truckService.getAvailableTrucksForDate(currentDate);

            double totalAvailableCapacity = availableTrucks.stream()
                    .mapToDouble(Truck::getContainerVolume)
                    .sum();

            if (totalAvailableCapacity >= totalOrderVolume) {
                availableDates.add(currentDate);
            }
        }

        return availableDates;
    }

    @Override
    @Transactional
    public void fulfillDueDeliveries() {
        List<Delivery> dueDeliveries = deliveryRepository
                .findByCompletedFalseAndDeliveryDateLessThanEqual(LocalDate.now());

        for (Delivery delivery : dueDeliveries) {
            delivery.setCompleted(true);
            deliveryRepository.save(delivery);

            Order order = delivery.getOrder();
            order.setStatus(OrderStatus.FULFILLED);
            orderRepository.save(order);
        }
    }
}