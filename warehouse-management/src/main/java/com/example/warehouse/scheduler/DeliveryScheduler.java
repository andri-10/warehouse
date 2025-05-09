package com.example.warehouse.scheduler;

import com.example.warehouse.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeliveryScheduler {

    private final OrderService orderService;

    @Autowired
    public DeliveryScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 0 0 * * *")  // Daily at midnight
    public void fulfillDueDeliveries() {
        orderService.fulfillDueDeliveries();
    }
}