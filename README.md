# 🍕 Food Delivery Platform

A production-grade food delivery backend built with Spring Boot microservices. Features JWT authentication, Eureka service discovery, Kafka event streaming, global exception handling, input validation, structured logging, and full Docker containerization.

---

## 📐 Architecture

```
Client
  │
  ▼
┌─────────────────────────────────────────────┐
│           API Gateway (Port 8080)            │
│   JWT Validation → Extract userId + role     │
│   Forward X-User-ID, X-User-Role headers     │
└──────────────┬──────────────────────────────┘
               │
       ┌───────┼────────────────────────┐
       ▼       ▼        ▼              ▼
  user-svc  restaurant  order-svc  delivery-svc
  (8081)    (8082)      (8083)     (8084)
     │         │            │
     ▼         ▼            │ Kafka
  user-db  restaurant-db    │ ──────────────────────────────────┐
               │            │                                    │
            order-db     "order.placed"                         │
                            │                                    │
                    ┌───────┴──────────┐                        │
                    ▼                  ▼                        │
             delivery-svc      notification-svc          notification-svc
             assigns partner    sends order SMS          sends delivery SMS
                    │
                    │ Kafka "order.delivered"
                    └──────────────────────────────────────────▶ notification-svc

All services register with:
└──▶ discovery-server (8761) — Eureka Registry
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 21 + Spring Boot 3.4.1 | Core framework |
| Spring Security + JWT (jjwt 0.12.6) | Authentication |
| Spring Cloud Netflix Eureka | Service discovery |
| Spring Cloud Gateway (MVC) | API routing + JWT validation |
| Spring Cloud OpenFeign | Synchronous inter-service HTTP |
| Apache Kafka 4.x (KRaft) | Async event streaming |
| PostgreSQL | One database per service |
| Docker + Docker Compose | Containerization |
| Lombok | Boilerplate reduction |
| BCrypt | Password hashing |
| SLF4J | Structured logging |
| Bean Validation (@Valid) | Input validation |

---

## 🏗️ Services

| Service | Port | Responsibility | Database |
|---------|------|----------------|----------|
| discovery-server | 8761 | Eureka service registry | None |
| api-gateway | 8080 | JWT validation, routing | None |
| user-service | 8081 | Registration, login, JWT | food_delivery_user_db |
| restaurant-service | 8082 | Restaurants, menus | food_delivery_restaurant_db |
| order-service | 8083 | Place orders, Kafka producer | food_delivery_order_db |
| delivery-service | 8084 | Assign partners, Kafka consumer/producer | food_delivery_delivery_db |
| notification-service | 8085 | SMS alerts via Kafka consumer | None |

---

## 🔑 Key Design Decisions

### JWT at the Gateway
Every request is validated once at the gateway. The gateway extracts `userId`, `role`, and `email` from the JWT and forwards them as HTTP headers to downstream services. Services never validate tokens themselves — they trust the headers.

```
Request → Gateway validates JWT → adds X-User-ID, X-User-Role headers → forwards to service
```

### Role-based Registration
Role is never passed in the request body. It is determined by which endpoint is called, hardcoded server-side:

```
POST /auth/register/customer          → role = CUSTOMER (always)
POST /auth/register/delivery-partner  → role = DELIVERY_PARTNER (always)
POST /auth/register/restaurant-owner  → role = RESTAURANT_OWNER (always)
ADMIN role → only via database seeder on startup
```

### Database per Service
Each service owns its own PostgreSQL database. No shared tables. No cross-service JPA joins. Cross-service data is fetched via REST (Feign) or stored as plain IDs.

### Data Snapshot Pattern
`OrderItem` stores `itemName` and `unitPrice` at the time of order placement. Even if a restaurant later changes their menu prices, all historical order data remains accurate.

### Kafka Event Flow
```
order-service publishes "order.placed"
        │
        ├──▶ delivery-service: auto-assigns available partner
        │
        └──▶ notification-service: sends order placed SMS

delivery-service publishes "order.delivered"
        │
        └──▶ notification-service: sends delivery confirmation SMS
```

### Mirror DTOs
Each service defines its own version of shared Kafka event classes (`OrderPlacedEvent`, `OrderDeliveredEvent`). Classes are never shared across service boundaries.

---

## 🚀 Running with Docker

### Prerequisites
- Docker Desktop installed and running

### Start everything
```bash
git clone https://github.com/AzizReja10/food-delivery.git
cd food-delivery
docker-compose up --build
```

All 12 containers start automatically:
- 4 PostgreSQL databases
- 1 Kafka broker (KRaft mode)
- 7 Spring Boot services

Gateway available at: `http://localhost:8080`
Eureka dashboard at: `http://localhost:8761`

### Stop everything
```bash
docker-compose down -v
```

---

## 💻 Running Locally

### Prerequisites
- Java 21
- PostgreSQL
- Apache Kafka 4.x

### 1. Create databases
```sql
CREATE DATABASE food_delivery_user_db;
CREATE DATABASE food_delivery_restaurant_db;
CREATE DATABASE food_delivery_order_db;
CREATE DATABASE food_delivery_delivery_db;
```

### 2. Start Kafka (KRaft mode)
```bash
# Format storage (first time only)
bin/windows/kafka-storage.bat format -t <uuid> --config config/server.properties --standalone

# Start Kafka
bin/windows/kafka-server-start.bat config/server.properties
```

### 3. Start services in order
```
1. discovery-server   (wait for port 8761)
2. user-service       (wait for port 8081)
3. restaurant-service (wait for port 8082)
4. order-service      (wait for port 8083)
5. delivery-service   (wait for port 8084)
6. notification-service (wait for port 8085)
7. api-gateway        (wait for port 8080)
```

---

## 📋 API Reference

All requests go through the gateway on port `8080`.

> **Note:** Protected endpoints require `Authorization: Bearer <token>` header.
> After login, the gateway automatically injects `X-User-ID` and `X-User-Role` into all downstream requests.

---

### 🔐 Authentication

#### Register as Customer
```
POST /auth/register/customer
Content-Type: application/json
```
```json
{
    "name": "Aziz Reja",
    "email": "aziz@gmail.com",
    "password": "password123",
    "phone": "9999999999"
}
```
**Response `201`:**
```
"Registration successful"
```

---

#### Register as Delivery Partner
```
POST /auth/register/delivery-partner
Content-Type: application/json
```
```json
{
    "name": "Rahul Kumar",
    "email": "rahul@gmail.com",
    "password": "password123",
    "phone": "8888888888"
}
```
**Response `201`:**
```
"Registration successful"
```

---

#### Register as Restaurant Owner
```
POST /auth/register/restaurant-owner
Content-Type: application/json
```
```json
{
    "name": "Suresh Patel",
    "email": "suresh@gmail.com",
    "password": "password123",
    "phone": "7777777777"
}
```
**Response `201`:**
```
"Registration successful"
```

---

#### Login
```
POST /auth/login
Content-Type: application/json
```
```json
{
    "email": "aziz@gmail.com",
    "password": "password123"
}
```
**Response `200`:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "aziz@gmail.com",
    "role": "CUSTOMER",
    "userId": 1
}
```

---

### 👤 Users

#### Get User Profile
```
GET /users/{id}
Authorization: Bearer <token>
```
**Response `200`:**
```json
{
    "id": 1,
    "name": "Aziz Reja",
    "email": "aziz@gmail.com",
    "phone": "9999999999",
    "role": "CUSTOMER",
    "active": true
}
```

---

#### Update User Role (Admin only)
```
PUT /users/admin/{id}/role?role=DELIVERY_PARTNER
Authorization: Bearer <admin token>
```
**Response `200`:** Updated user object

**Response `403` (non-admin):**
```json
{
    "status": 403,
    "error": "FORBIDDEN",
    "message": "Access denied — Admin role required"
}
```

---

#### Add Address
```
POST /users/{id}/addresses
Authorization: Bearer <token>
Content-Type: application/json
```
```json
{
    "street": "123 Main Street",
    "city": "Kolkata",
    "state": "West Bengal",
    "zipcode": "700001",
    "isDefault": true
}
```
**Response `200`:** Created address object

---

#### Get Addresses
```
GET /users/{id}/addresses
Authorization: Bearer <token>
```
**Response `200`:** List of addresses

---

### 🍽️ Restaurants

#### Create Restaurant (Restaurant Owner only)
```
POST /restaurants
Authorization: Bearer <restaurant owner token>
Content-Type: application/json
```
```json
{
    "name": "Spice Garden",
    "address": "123 Park Street",
    "city": "Kolkata",
    "phone": "9111111111",
    "cuisineType": "INDIAN",
    "imageUrl": "https://example.com/spicegarden.jpg"
}
```
**Available cuisine types:** `INDIAN`, `CHINESE`, `ITALIAN`, `MEXICAN`, `AMERICAN`, `JAPANESE`, `THAI`, `MEDITERRANEAN`, `FAST_FOOD`, `OTHER`

**Response `201`:**
```json
{
    "id": 1,
    "name": "Spice Garden",
    "ownerId": 2,
    "address": "123 Park Street",
    "city": "Kolkata",
    "phone": "9111111111",
    "cuisineType": "INDIAN",
    "isOpen": true,
    "rating": 0.0
}
```

---

#### Get All Restaurants
```
GET /restaurants
Authorization: Bearer <token>
```
**Response `200`:** List of all active restaurants

---

#### Get Open Restaurants
```
GET /restaurants/open
Authorization: Bearer <token>
```
**Response `200`:** List of currently open restaurants

---

#### Get Restaurants by City
```
GET /restaurants/city/{city}
Authorization: Bearer <token>
```
**Example:** `GET /restaurants/city/Kolkata`

**Response `200`:** List of restaurants in that city

---

#### Get Restaurants by Cuisine
```
GET /restaurants/cuisine/{cuisineType}
Authorization: Bearer <token>
```
**Example:** `GET /restaurants/cuisine/INDIAN`

**Response `200`:** List of restaurants with that cuisine

---

#### Get My Restaurants (Owner)
```
GET /restaurants/my
Authorization: Bearer <restaurant owner token>
```
**Response `200`:** List of restaurants owned by the authenticated user

---

#### Get Restaurant by ID
```
GET /restaurants/{id}
Authorization: Bearer <token>
```
**Response `200`:** Single restaurant object

---

#### Toggle Restaurant Open/Closed
```
PATCH /restaurants/{id}/toggle-open
Authorization: Bearer <restaurant owner token>
```
**Response `200`:** Updated restaurant with new `isOpen` value

---

#### Delete Restaurant (soft delete)
```
DELETE /restaurants/{id}
Authorization: Bearer <restaurant owner token>
```
**Response `200`:**
```
"Restaurant deleted"
```

---

### 🍛 Menu Items

#### Add Menu Item
```
POST /restaurants/{restaurantId}/menu
Authorization: Bearer <restaurant owner token>
Content-Type: application/json
```
```json
{
    "name": "Butter Chicken",
    "description": "Creamy tomato based chicken curry",
    "price": 320.00,
    "category": "Main Course",
    "imageUrl": "https://example.com/butter-chicken.jpg",
    "isVeg": false
}
```
**Response `201`:**
```json
{
    "id": 1,
    "restaurantId": 1,
    "restaurantName": "Spice Garden",
    "name": "Butter Chicken",
    "description": "Creamy tomato based chicken curry",
    "price": 320.00,
    "category": "Main Course",
    "isAvailable": true,
    "isVeg": false
}
```

---

#### Get Full Menu
```
GET /restaurants/{restaurantId}/menu
Authorization: Bearer <token>
```
**Response `200`:** List of all available menu items

---

#### Get Menu by Category
```
GET /restaurants/{restaurantId}/menu/category/{category}
Authorization: Bearer <token>
```
**Example:** `GET /restaurants/1/menu/category/Main Course`

**Response `200`:** List of menu items in that category

---

#### Toggle Menu Item Availability
```
PATCH /restaurants/menu/{itemId}/toggle-availability
Authorization: Bearer <restaurant owner token>
```
**Response `200`:** Updated menu item with new `isAvailable` value

---

### 📦 Orders

#### Place Order
```
POST /orders
Authorization: Bearer <customer token>
Content-Type: application/json
```
```json
{
    "restaurantId": 1,
    "deliveryAddress": "123 Main St, Kolkata",
    "specialInstructions": "Extra spicy please",
    "items": [
        {
            "menuItemId": 1,
            "quantity": 2
        },
        {
            "menuItemId": 3,
            "quantity": 1
        }
    ]
}
```
**Response `201`:**
```json
{
    "id": 1,
    "customerId": 1,
    "restaurantId": 1,
    "restaurantName": "Spice Garden",
    "deliveryAddress": "123 Main St, Kolkata",
    "status": "PENDING",
    "totalAmount": 700.00,
    "specialInstructions": "Extra spicy please",
    "items": [
        {
            "id": 1,
            "menuItemId": 1,
            "itemName": "Butter Chicken",
            "unitPrice": 320.00,
            "quantity": 2,
            "lineTotal": 640.00
        },
        {
            "id": 2,
            "menuItemId": 3,
            "itemName": "Garlic Naan",
            "unitPrice": 60.00,
            "quantity": 1,
            "lineTotal": 60.00
        }
    ],
    "createdAt": "2026-07-11T09:23:53"
}
```

> **Note:** Placing an order automatically triggers a Kafka `order.placed` event which:
> - Assigns an available delivery partner (delivery-service)
> - Sends an SMS notification to the customer (notification-service)

---

#### Get My Orders
```
GET /orders
Authorization: Bearer <customer token>
```
**Response `200`:** List of all orders for the authenticated customer

---

#### Get Order by ID
```
GET /orders/{orderId}
Authorization: Bearer <token>
```
**Response `200`:** Single order object

---

#### Update Order Status
```
PATCH /orders/{orderId}/status?status=CONFIRMED
Authorization: Bearer <token>
```
**Available statuses:** `PENDING`, `CONFIRMED`, `PREPARING`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`

**Response `200`:** Updated order object

---

#### Cancel Order
```
PATCH /orders/{orderId}/cancel
Authorization: Bearer <customer token>
```
> Only orders in `PENDING` status can be cancelled.

**Response `200`:** Updated order with `status: CANCELLED`

**Response `400` (already confirmed):**
```json
{
    "status": 400,
    "error": "BAD_REQUEST",
    "message": "Cannot cancel order in CONFIRMED status"
}
```

---

### 🚴 Deliveries

#### Register as Delivery Partner
```
POST /deliveries/partners/register
Authorization: Bearer <delivery partner token>
Content-Type: application/json
```
```json
{
    "name": "Rahul Kumar",
    "phone": "8888888888"
}
```
**Response `201`:**
```json
{
    "id": 1,
    "userId": 3,
    "name": "Rahul Kumar",
    "phone": "8888888888",
    "isAvailable": true,
    "totalDeliveries": 0
}
```

---

#### Get Available Partners
```
GET /deliveries/partners/available
```
> This endpoint is **public** — no token required.

**Response `200`:** List of available delivery partners

---

#### Get Delivery by Order ID
```
GET /deliveries/order/{orderId}
Authorization: Bearer <token>
```
**Response `200`:**
```json
{
    "id": 1,
    "orderId": 1,
    "partnerId": 1,
    "customerId": 1,
    "restaurantId": 1,
    "restaurantName": "Spice Garden",
    "deliveryAddress": "123 Main St, Kolkata",
    "status": "ASSIGNED",
    "assignedAt": "2026-07-11T09:23:53",
    "pickedUpAt": null,
    "deliveredAt": null
}
```

---

#### Get My Deliveries (Partner)
```
GET /deliveries/my
Authorization: Bearer <delivery partner token>
```
**Response `200`:** List of all deliveries assigned to the authenticated partner

---

#### Update Delivery Status
```
PATCH /deliveries/{deliveryId}/status?status=PICKED_UP
Authorization: Bearer <delivery partner token>
```
**Delivery status flow:**
```
ASSIGNED → PICKED_UP → OUT_FOR_DELIVERY → DELIVERED
                                        → FAILED
```

**Response `200`:** Updated delivery object

> **Note:** When status is set to `DELIVERED`:
> - Partner's `isAvailable` is reset to `true`
> - Partner's `totalDeliveries` is incremented
> - Kafka `order.delivered` event is published
> - Customer receives delivery confirmation SMS (notification-service)

---

## ❌ Error Responses

All errors return a consistent JSON format:

```json
{
    "status": 400,
    "error": "BAD_REQUEST",
    "message": "Detailed error message here",
    "timestamp": "2026-07-11 09:23:53"
}
```

### Error codes

| Status | Error | When |
|--------|-------|------|
| 400 | `BAD_REQUEST` | Invalid business logic |
| 400 | `VALIDATION_FAILED` | Missing/invalid request fields |
| 401 | `UNAUTHORIZED` | Invalid credentials or missing token |
| 403 | `FORBIDDEN` | Insufficient role permissions |
| 404 | `NOT_FOUND` | Resource doesn't exist |
| 500 | `INTERNAL_SERVER_ERROR` | Unexpected server error |

### Validation error example

```json
{
    "status": 400,
    "error": "VALIDATION_FAILED",
    "message": "Name is required, Email is required, Password must be at least 6 characters",
    "timestamp": "2026-07-11 09:23:53"
}
```

---

## 📊 Kafka Topics

| Topic | Published by | Consumed by | Payload |
|-------|-------------|-------------|---------|
| `order.placed` | order-service | delivery-service, notification-service | orderId, customerId, restaurantId, restaurantName, totalAmount, deliveryAddress |
| `order.delivered` | delivery-service | notification-service | orderId, customerId, partnerId, deliveredAt |

---

## 🔄 Complete Order Flow

```
1. Customer registers    → POST /auth/register/customer
2. Customer logs in      → POST /auth/login → gets JWT token
3. Browse restaurants    → GET /restaurants
4. View menu             → GET /restaurants/1/menu
5. Place order           → POST /orders
                              │
                              ├──▶ Kafka "order.placed" fires
                              │         ├──▶ delivery-service assigns partner
                              │         └──▶ notification-service sends SMS
                              │
6. Check delivery        → GET /deliveries/order/{orderId}
7. Partner picks up      → PATCH /deliveries/{id}/status?status=PICKED_UP
8. Partner delivers      → PATCH /deliveries/{id}/status?status=DELIVERED
                              │
                              └──▶ Kafka "order.delivered" fires
                                        └──▶ notification-service sends SMS
```

---

## 🏠 Project Structure

```
food-delivery/
├── discovery-server/
│   └── src/main/java/com/fooddelivery/discovery/
│       └── DiscoveryServerApplication.java
├── api-gateway/
│   └── src/main/java/com/fooddelivery/gateway/
│       ├── ApiGatewayApplication.java
│       ├── GatewayConfig.java
│       └── security/
│           ├── JwtUtil.java
│           ├── JwtAuthFilter.java
│           └── MutableHttpServletRequest.java
├── user-service/
│   └── src/main/java/com/fooddelivery/user/
│       ├── entity/        (User, Address, UserRole)
│       ├── dto/           (RegisterRequest, LoginRequest, AuthResponse, ...)
│       ├── repository/    (UserRepo, AddressRepo)
│       ├── security/      (JwtUtil, JwtAuthFilter, SecurityConfig, UserDetailsServiceImpl)
│       ├── service/       (UserService, DataSeeder)
│       ├── controller/    (AuthController, UserController)
│       └── exception/     (GlobalExceptionHandler)
├── restaurant-service/
│   └── src/main/java/com/fooddelivery/restaurant/
│       ├── entity/        (Restaurant, MenuItem, CuisineType)
│       ├── dto/           (RestaurantRequest, RestaurantResponse, ...)
│       ├── repository/    (RestaurantRepo, MenuItemRepo)
│       ├── service/       (RestaurantService)
│       ├── controller/    (RestaurantController)
│       └── exception/     (GlobalExceptionHandler)
├── order-service/
│   └── src/main/java/com/fooddelivery/order/
│       ├── entity/        (Order, OrderItem, OrderStatus)
│       ├── dto/           (OrderRequest, OrderResponse, OrderPlacedEvent, ...)
│       ├── client/        (RestaurantClient)
│       ├── config/        (KafkaConfig)
│       ├── repository/    (OrderRepo)
│       ├── service/       (OrderService)
│       ├── controller/    (OrderController)
│       └── exception/     (GlobalExceptionHandler)
├── delivery-service/
│   └── src/main/java/com/fooddelivery/delivery/
│       ├── entity/        (Delivery, DeliveryPartner, DeliveryStatus)
│       ├── dto/           (OrderPlacedEvent, OrderDeliveredEvent, ...)
│       ├── config/        (KafkaConsumerConfig)
│       ├── repository/    (DeliveryRepo, DeliveryPartnerRepo)
│       ├── service/       (DeliveryService)
│       ├── controller/    (DeliveryController)
│       └── exception/     (GlobalExceptionHandler)
├── notification-service/
│   └── src/main/java/com/fooddelivery/notification/
│       ├── dto/           (OrderPlacedEvent, OrderDeliveredEvent)
│       ├── config/        (KafkaConsumerConfig)
│       └── service/       (NotificationService)
└── docker-compose.yml
```

---

## 👨‍💻 Author

**Aziz Reja** — 3rd year student at Jadavpur University

- GitHub: [@AzizReja10](https://github.com/AzizReja10)
- LeetCode: [AzizReja](https://leetcode.com/AzizReja)