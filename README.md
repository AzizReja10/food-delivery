# food-delivery

A production-ready food delivery platform built with Spring Boot microservices. Features JWT authentication, Eureka service discovery, Kafka event streaming, and OpenFeign inter-service communication — all containerized with Docker.

## Architecture

```
Client
  │
  ▼
API Gateway (8080) — JWT validation + routing
  │
  ├──▶ user-service       (8081) ──▶ user-db
  ├──▶ restaurant-service (8082) ──▶ restaurant-db
  ├──▶ order-service      (8083) ──▶ order-db
  ├──▶ delivery-service   (8084) ──▶ delivery-db
  └──▶ notification-service (8085) — no DB
  
Discovery Server (8761) — Eureka registry

Kafka Event Flow:
order-service ──▶ "order.placed" ──▶ delivery-service (assigns partner)
                                 └──▶ notification-service (SMS alert)

delivery-service ──▶ "order.delivered" ──▶ notification-service (SMS alert)
```

## Services

| Service | Port | Responsibility |
|---------|------|----------------|
| discovery-server | 8761 | Eureka service registry |
| api-gateway | 8080 | JWT validation, request routing |
| user-service | 8081 | Registration, login, JWT generation |
| restaurant-service | 8082 | Restaurants, menus, cuisines |
| order-service | 8083 | Place orders, Kafka producer |
| delivery-service | 8084 | Assign partners, Kafka consumer + producer |
| notification-service | 8085 | Kafka consumer, SMS simulation |

## Tech Stack

- **Java 21** + **Spring Boot 3.4.1**
- **Spring Security** + **JWT (jjwt 0.12.6)** — authentication
- **Spring Cloud Netflix Eureka** — service discovery
- **Spring Cloud Gateway (MVC)** — API gateway
- **Spring Cloud OpenFeign** — synchronous inter-service calls
- **Apache Kafka** — async event streaming
- **Spring Data JPA** + **Hibernate** — ORM
- **PostgreSQL** — one database per service
- **Lombok** — boilerplate reduction

## Key Features

### JWT Authentication
- Gateway validates JWT on every request
- Extracts `userId`, `role`, `email` from token
- Forwards as `X-User-ID`, `X-User-Role`, `X-User-Email` headers
- Downstream services never touch JWT — zero coupling

### Role-Based Registration
```
POST /auth/register/customer          → CUSTOMER role
POST /auth/register/delivery-partner  → DELIVERY_PARTNER role
POST /auth/register/restaurant-owner  → RESTAURANT_OWNER role
```
Role is determined by endpoint — never by request body.

### Kafka Event Flow
```
Customer places order
      │
      ▼
order-service saves order
      └──▶ publishes OrderPlacedEvent to "order.placed"
                  │
                  ├──▶ delivery-service
                  │         └── assigns available delivery partner
                  │
                  └──▶ notification-service
                            └── SMS: "Your order has been placed!"

Delivery partner marks as DELIVERED
      │
      ▼
delivery-service publishes OrderDeliveredEvent to "order.delivered"
      └──▶ notification-service
                └── SMS: "Your order has been delivered!"
```

### Service Discovery
All services register with Eureka on startup. No hardcoded URLs between services — Feign clients resolve by service name automatically.

## Running Locally

### Prerequisites
- Java 21
- PostgreSQL
- Apache Kafka 4.x (KRaft mode)

### Create databases
```sql
CREATE DATABASE food_delivery_user_db;
CREATE DATABASE food_delivery_restaurant_db;
CREATE DATABASE food_delivery_order_db;
CREATE DATABASE food_delivery_delivery_db;
```

### Start Kafka (KRaft mode)
```cmd
cd C:\kafka\bin
windows\kafka-server-start.bat ..\config\server.properties
```

### Start services in order
```
1. discovery-server   → wait for port 8761
2. user-service       → wait for port 8081
3. restaurant-service → wait for port 8082
4. order-service      → wait for port 8083
5. delivery-service   → wait for port 8084
6. notification-service → wait for port 8085
7. api-gateway        → wait for port 8080
```

Check Eureka dashboard: `http://localhost:8761`

## API Endpoints

All requests go through the gateway on port `8080`.

### Auth (public — no token needed)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register/customer` | Register as customer |
| POST | `/auth/register/delivery-partner` | Register as delivery partner |
| POST | `/auth/register/restaurant-owner` | Register as restaurant owner |
| POST | `/auth/login` | Login — returns JWT token |

### Restaurants
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/restaurants` | Create restaurant (owner) |
| GET | `/restaurants` | Get all restaurants |
| GET | `/restaurants/open` | Get open restaurants |
| GET | `/restaurants/city/{city}` | Filter by city |
| GET | `/restaurants/cuisine/{type}` | Filter by cuisine |
| GET | `/restaurants/my` | Get my restaurants (owner) |
| PATCH | `/restaurants/{id}/toggle-open` | Open/close restaurant |
| POST | `/restaurants/{id}/menu` | Add menu item |
| GET | `/restaurants/{id}/menu` | Get menu |

### Orders
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/orders` | Place order → triggers Kafka |
| GET | `/orders` | Get my orders |
| GET | `/orders/{id}` | Get order by ID |
| PATCH | `/orders/{id}/cancel` | Cancel order |

### Deliveries
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/deliveries/partners/register` | Register as delivery partner |
| GET | `/deliveries/partners/available` | Get available partners |
| GET | `/deliveries/order/{orderId}` | Get delivery by order |
| GET | `/deliveries/my` | Get my deliveries (partner) |
| PATCH | `/deliveries/{id}/status` | Update delivery status |

## Example Flow

```
1. Register customer     → POST /auth/register/customer
2. Login                 → POST /auth/login → get JWT token
3. Browse restaurants    → GET /restaurants
4. View menu             → GET /restaurants/1/menu
5. Place order           → POST /orders (Kafka fires automatically)
6. Track delivery        → GET /deliveries/order/{orderId}
7. Partner delivers      → PATCH /deliveries/{id}/status?status=DELIVERED
8. SMS notification      → notification-service logs the alert
```

## Kafka Topics

| Topic | Producer | Consumers |
|-------|----------|-----------|
| `order.placed` | order-service | delivery-service, notification-service |
| `order.delivered` | delivery-service | notification-service |

## Design Patterns Used

**Database per service** — each service owns its own PostgreSQL instance, no shared tables

**API Gateway pattern** — single entry point, JWT validated once at the edge

**Event-driven architecture** — order placement triggers async Kafka events across services

**Data snapshot pattern** — `OrderItem` stores `itemName` and `unitPrice` at order time, preserving history even if menu changes

**Role-based registration** — role determined by endpoint, never by request body

## Author

**Aziz Reja** — 3rd year student at Jadavpur University
GitHub: [@AzizReja10](https://github.com/AzizReja10)
LeetCode: [AzizReja](https://leetcode.com/AzizReja)
