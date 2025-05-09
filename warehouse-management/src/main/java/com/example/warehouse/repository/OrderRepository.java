package com.example.warehouse.repository;

import com.example.warehouse.entity.Order;
import com.example.warehouse.entity.OrderStatus;
import com.example.warehouse.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientOrderBySubmittedDateDesc(User client);
    List<Order> findByClientAndStatusOrderBySubmittedDateDesc(User client, OrderStatus status);
    List<Order> findByStatusOrderBySubmittedDateDesc(OrderStatus status);
    Optional<Order> findByOrderNumber(String orderNumber);
}