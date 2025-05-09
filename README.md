# Warehouse Management System

A Spring Boot application for managing warehouse inventory, orders, and deliveries.

## Features

- **User Management**: Support for CLIENT, WAREHOUSE_MANAGER, and SYSTEM_ADMIN roles
- **Order Management**: Complete order lifecycle from creation to fulfillment
- **Inventory Management**: Track items, quantities, and packaging volumes
- **Delivery Scheduling**: Schedule deliveries based on truck availability and capacity
- **REST API**: Comprehensive API with JWT authentication

## Technologies

- Spring Boot 3.x
- Spring Security with JWT
- Spring Data JPA
- MySQL Database
- Swagger/OpenAPI Documentation
- Maven

## Getting Started

### Prerequisites

- Java 17+
- MySQL 8.0+
- Maven

### Database Configuration

Configure your MySQL database connection in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/springstudent?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=springstudent
spring.datasource.password=springstudent
```

### Running the Application

```bash
mvn spring:boot run
```

The application will start on port 8080 with default test users created.

### Default Users

- **Admin**: username: `admin`, password: `admin123`
- **Manager**: username: `manager`, password: `manager123`
- **Client**: username: `client`, password: `client123`

## API Documentation

Swagger UI is available at:
```
http://localhost:8080/swagger-ui.html
```

## Testing with Postman

1. Authenticate using the login endpoint
2. Use the returned JWT token for subsequent requests
3. Follow the order lifecycle: create → add items → submit → approve → schedule → fulfill

## Project Structure

- `entity`: Domain models (User, Order, Item, etc.)
- `repository`: Data access layer
- `service`: Business logic
- `controller`: REST endpoints
- `security`: JWT authentication and authorization
