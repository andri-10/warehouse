package com.example.warehouse.config;

import com.example.warehouse.entity.*;
import com.example.warehouse.service.ItemService;
import com.example.warehouse.service.TruckService;
import com.example.warehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            @Autowired UserService userService,
            @Autowired ItemService itemService,
            @Autowired TruckService truckService) {

        return args -> {
            if (!userService.existsUserByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setEmail("admin@example.com");
                admin.setFullName("Admin User");
                admin.setRole(UserRole.SYSTEM_ADMIN);
                userService.createUser(admin);
                System.out.println("Admin user created");
            }

            if (!userService.existsUserByUsername("manager")) {
                User manager = new User();
                manager.setUsername("manager");
                manager.setPassword("manager123");
                manager.setEmail("manager@example.com");
                manager.setFullName("Warehouse Manager");
                manager.setRole(UserRole.WAREHOUSE_MANAGER);
                userService.createUser(manager);
                System.out.println("Manager user created");
            }

            if (!userService.existsUserByUsername("client")) {
                User client = new User();
                client.setUsername("client");
                client.setPassword("client123");
                client.setEmail("client@example.com");
                client.setFullName("Client User");
                client.setRole(UserRole.CLIENT);
                userService.createUser(client);
                System.out.println("Client user created");
            }

            if (itemService.getAllItems().isEmpty()) {
                Item laptop = new Item();
                laptop.setName("Laptop");
                laptop.setQuantity(50);
                laptop.setUnitPrice(999.99);
                laptop.setPackageVolume(0.05);
                itemService.createItem(laptop);

                Item chair = new Item();
                chair.setName("Office Chair");
                chair.setQuantity(30);
                chair.setUnitPrice(199.99);
                chair.setPackageVolume(0.3);
                itemService.createItem(chair);

                Item desk = new Item();
                desk.setName("Desk");
                desk.setQuantity(25);
                desk.setUnitPrice(299.99);
                desk.setPackageVolume(0.5);
                itemService.createItem(desk);

                System.out.println("Sample items created");
            }

            if (truckService.getAllTrucks().isEmpty()) {
                Truck smallTruck = new Truck();
                smallTruck.setLicensePlate("ABC123");
                smallTruck.setContainerVolume(10.0);
                truckService.createTruck(smallTruck);

                Truck mediumTruck = new Truck();
                mediumTruck.setLicensePlate("DEF456");
                mediumTruck.setContainerVolume(20.0);
                truckService.createTruck(mediumTruck);

                Truck largeTruck = new Truck();
                largeTruck.setLicensePlate("GHI789");
                largeTruck.setContainerVolume(30.0);
                truckService.createTruck(largeTruck);

                System.out.println("Sample trucks created");
            }
        };
    }
}