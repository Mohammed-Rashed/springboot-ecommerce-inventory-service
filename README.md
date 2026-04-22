# Inventory Service API

## Overview

Inventory Service is responsible for managing product stock in the system.

## Technologies Used

This project was built using the following technologies and concepts:

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Jakarta Validation
- Lombok
- Maven
- RESTful APIs
- Postman for API testing
- Git for version control

## Implemented Concepts

- Layered Architecture (Controller, Service, Repository, DTO)
- Global Exception Handling
- Request Validation
- Database Migration with Flyway
- Inventory Management Flow
- Atomic Update Queries for stock operations
- Basic Microservice Design Principles



Current MVP features:

* Create stock for a product
* Update stock
* Get stock by product ID
* Check stock availability
* Reserve stock
* Release reserved stock
* Deduct reserved stock

Base URL:

```text
http://localhost:8084/api/v1/inventory
```

---

## Data Model

Each inventory record contains:

* `id`: Inventory row ID
* `productId`: Product identifier from Product Service
* `sku`: Product SKU
* `availableQuantity`: Quantity currently available for sale
* `reservedQuantity`: Quantity temporarily reserved for pending orders
* `updatedAt`: Last update timestamp

---

## Stock Flow

Typical order flow:

```text
check availability -> reserve -> deduct
```

If order/payment fails:

```text
check availability -> reserve -> release
```

Meaning:

* `reserve`: Move quantity from available stock to reserved stock
* `release`: Return reserved quantity back to available stock
* `deduct`: Finalize the sale by reducing reserved quantity only

---

## Endpoints

### 1) Create Inventory

**POST** `/api/v1/inventory`

Creates a stock record for a product.

#### Request Body

```json
{
  "productId": 1,
  "sku": "SKU-001",
  "availableQuantity": 10
}
```

#### Success Response

**201 Created**

```json
{
  "id": 1,
  "productId": 1,
  "sku": "SKU-001",
  "availableQuantity": 10,
  "reservedQuantity": 0,
  "updatedAt": "2026-04-22T22:10:00"
}
```

#### Notes

* `productId` must be unique
* `sku` must be unique
* `availableQuantity` must be greater than or equal to `0`

---

### 2) Update Inventory

**PUT** `/api/v1/inventory/{productId}`

Updates inventory data for a given product.

#### Path Variable

* `productId`: Product identifier

#### Request Body

```json
{
  "sku": "SKU-001-UPDATED",
  "availableQuantity": 25
}
```

#### Success Response

**200 OK**

```json
{
  "id": 1,
  "productId": 1,
  "sku": "SKU-001-UPDATED",
  "availableQuantity": 25,
  "reservedQuantity": 0,
  "updatedAt": "2026-04-22T22:15:00"
}
```

---

### 3) Get Inventory By Product ID

**GET** `/api/v1/inventory/{productId}`

Returns inventory details for a given product.

#### Success Response

**200 OK**

```json
{
  "id": 1,
  "productId": 1,
  "sku": "SKU-001",
  "availableQuantity": 10,
  "reservedQuantity": 0,
  "updatedAt": "2026-04-22T22:10:00"
}
```

---

### 4) Check Availability

**POST** `/api/v1/inventory/check`

Checks whether the requested quantity is available.

#### Request Body

```json
{
  "productId": 1,
  "requestedQuantity": 5
}
```

#### Success Response

**200 OK**

```json
{
  "productId": 1,
  "requestedQuantity": 5,
  "availableQuantity": 10,
  "available": true
}
```

---

### 5) Reserve Stock

**POST** `/api/v1/inventory/reserve`

Temporarily reserves stock for a pending order.

#### Request Body

```json
{
  "productId": 1,
  "quantity": 3
}
```

#### Success Response

**200 OK**

```json
{
  "productId": 1,
  "sku": "SKU-001",
  "availableQuantity": 7,
  "reservedQuantity": 3,
  "message": "Stock reserved successfully",
  "updatedAt": "2026-04-22T22:30:00"
}
```

#### Business Rule

* `availableQuantity` decreases
* `reservedQuantity` increases

---

### 6) Release Stock

**POST** `/api/v1/inventory/release`

Releases reserved stock back to available stock.

#### Request Body

```json
{
  "productId": 1,
  "quantity": 2
}
```

#### Success Response

**200 OK**

```json
{
  "productId": 1,
  "sku": "SKU-001",
  "availableQuantity": 9,
  "reservedQuantity": 1,
  "message": "Stock released successfully",
  "updatedAt": "2026-04-22T22:31:00"
}
```

#### Business Rule

* `availableQuantity` increases
* `reservedQuantity` decreases

---

### 7) Deduct Stock

**POST** `/api/v1/inventory/deduct`

Finalizes stock consumption after order confirmation.

#### Request Body

```json
{
  "productId": 1,
  "quantity": 1
}
```

#### Success Response

**200 OK**

```json
{
  "productId": 1,
  "sku": "SKU-001",
  "availableQuantity": 9,
  "reservedQuantity": 0,
  "message": "Stock deducted successfully",
  "updatedAt": "2026-04-22T22:32:00"
}
```

#### Business Rule

* `reservedQuantity` decreases only
* `availableQuantity` does not change because it was already reduced during reserve

---

## Error Responses

### Validation Error

**400 Bad Request**

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-04-22T22:40:00",
  "errors": {
    "productId": "productId is required",
    "sku": "sku is required",
    "availableQuantity": "availableQuantity must be greater than or equal to 0"
  }
}
```

### Not Found

**404 Not Found**

```json
{
  "status": 404,
  "message": "Inventory not found for productId: 999",
  "timestamp": "2026-04-22T22:40:00",
  "errors": null
}
```

### Conflict

**409 Conflict**

Example cases:

* Inventory already exists for the same `productId`
* Inventory already exists for the same `sku`
* Insufficient available stock for reserve
* Insufficient reserved stock for release/deduct

Example response:

```json
{
  "status": 409,
  "message": "Insufficient available stock for productId: 1",
  "timestamp": "2026-04-22T22:40:00",
  "errors": null
}
```

---

## Important Notes

* Inventory Service is independent from Product Service
* `productId` is stored as a plain field, not as a JPA relationship
* `reserve`, `release`, and `deduct` use atomic update queries to reduce concurrency issues
* `reservedQuantity` is used to protect stock during pending order/payment flow

---

## Future Improvements

Possible next enhancements:

* Add Swagger / OpenAPI documentation
* Add integration tests
* Add authentication/authorization
* Publish stock events via Kafka
* Sync stock changes with Order Service
