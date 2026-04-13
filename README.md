# Product Flow MS

A Spring Cloud microservices application simulating a fashion e-commerce backend. Services handle product catalog browsing, inventory availability, payment processing, and order orchestration - all wired together via service discovery, centralized configuration, and an API gateway.

## Technology Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Runtime |
| Spring Boot | 4.0.3 / 4.0.5 | Service framework |
| Spring Cloud | 2025.1.0 / 2025.1.1 | Cloud-native patterns |
| Spring Cloud Netflix Eureka | - | Service discovery |
| Spring Cloud Config Server | - | Centralized configuration |
| Spring Cloud Gateway (WebMVC) | - | API gateway |
| Spring Cloud OpenFeign | - | Declarative HTTP clients |
| Resilience4j | - | Circuit breaker & retry |
| Gradle | - | Build tool |

---

## Architecture Overview

### Infrastructure Services (all business services connect to these)

```
  ┌──────────────────────────┐        ┌──────────────────────────┐
  │    registry-service      │        │      config-server       │
  │    (Eureka Server)       │        │   (Config Server)        │
  │        :8761             │        │        :8888             │
  └──────────────────────────┘        └──────────────────────────┘
      ▲  all services register here       ▲  all services fetch config here
```

### Business Services & Request Flow

```
  Client
    │
    ▼
  ┌───────────────────────────────────────────────────────┐
  │                   gateway-service                     │  :8080
  │               (Spring Cloud Gateway)                  │
  └──────────┬──────────────┬──────────────┬──────────────┘
             │              │              │
             ▼              ▼              ▼
  ┌────────────────┐ ┌────────────────-┐ ┌────────────────┐
  │catalog-service │ │inventory-service│ │ order-service  │
  │    :8081       │ │    :8082        │ │    :8084       │
  └────────────────┘ └────────┬──────-─┘ └──────-─┬───────┘------|
           ▲                  │ Feign             │              │
           │                  │ (on startup:      │ Feign:       │ Feign:
           └──────────────────┘ load products)    │ check        │ process
                                                  │ availability │ payment
                                                  ▼              ▼
                                        ┌───────────────-─┐ ┌────────────────┐
                                        │inventory-service│ │payment-service │
                                        │    :8082        │ │    :8083       │
                                        └───────────-─────┘ └────────────────┘
                                                            (not routed via gateway)
```

### Request Flow

1. Client sends a request to the **gateway** on port `8080`
2. Gateway routes to the appropriate service using load-balanced Eureka names (`lb://service-name`)
3. **order-service** orchestrates a complete order:
   - Calls **inventory-service** to verify product availability
   - Calls **payment-service** to process payment
   - Returns `CONFIRMED`, `REJECTED_UNAVAILABLE`, or `REJECTED_PAYMENT_FAILED`
4. **inventory-service** initializes its in-memory store by calling **catalog-service** on startup (with circuit breaker + retry protection)

---

## Services

### registry-service - Port 8761

Eureka Server. All other services register here and discover each other through it.

No business logic. Acts as the central service registry.

---

### config-server - Port 8888

Spring Cloud Config Server using the `native` profile. Serves configuration files from `classpath:/config/`.

| Config File | Applies To |
|---|---|
| `application.yml` | All services (Eureka settings) |
| `gateway-service.yml` | Gateway port + route definitions |
| `catalog-service.yml` | Catalog port (8081) |
| `inventory-service.yml` | Inventory port (8082) + Resilience4j settings |
| `payment-service.yml` | Payment port (8083) |
| `order-service.yml` | Order port (8084) |

---

### gateway-service - Port 8080

Spring Cloud Gateway (WebMVC mode). Routes are defined in `config-server` and resolved via Eureka.

| Route | Target |
|---|---|
| `/api/catalog/**` | `lb://catalog-service` |
| `/api/inventory/**` | `lb://inventory-service` |

Payment and order routes are currently commented out in `gateway-service.yml`.

---

### catalog-service - Port 8081

Serves product data loaded from a CSV file at startup. No database.

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/catalog` | List all products. Supports `?category=` and `?brand=` query parameters. |
| `GET` | `/api/catalog/{id}` | Get a single product by ID. |

**Product fields:** `productId`, `name`, `brand`, `category`, `price`, `rating`, `color`, `size`

**Data source:** `src/main/resources/data/fashion_products.csv` (fashion products dataset)

---

### inventory-service - Port 8082

Maintains an in-memory map of product availability (random boolean per product). Initializes by fetching all products from catalog-service on startup.

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/inventory/{productId}` | Returns `{ productId, available }` |

**Resilience4j configuration (from config-server):**

| Setting | Value |
|---|---|
| Retry max attempts | 10 |
| Retry wait duration | 4 seconds |
| Circuit breaker type | COUNT_BASED |
| Sliding window size | 6 calls |
| Minimum calls | 2 |
| Failure rate threshold | 50% |
| Wait in OPEN state | 5 seconds |

The `initialize()` method is annotated with `@Retry` and `@CircuitBreaker`. If catalog-service is unreachable after all retries, the circuit opens and a fallback runs (empty inventory).

---

### payment-service - Port 8083

Simulates payment processing. Returns a random success/failure result.

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/payments/process` | Process a payment. Returns `{ paymentId, orderId, success }` |

**Request body:** `{ "orderId": "...", "productId": 123 }`

---

### order-service - Port 8084

Orchestrates the order creation flow. Calls inventory-service and payment-service via Feign clients.

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/orders` | Create an order. Returns HTTP 201. |

**Request body:** `{ "productId": 123 }`

**Order statuses:**

| Status | Condition |
|---|---|
| `CONFIRMED` | Product available and payment successful |
| `REJECTED_UNAVAILABLE` | Product not in inventory or not found in catalog |
| `REJECTED_PAYMENT_FAILED` | Payment service returned `success: false` |

---

## Startup Order

Services must be started in this order due to dependencies:

```
1. registry-service    (no dependencies)
2. config-server       (needs Eureka)
3. gateway-service     (needs Config Server + Eureka)
4. catalog-service     (needs Config Server + Eureka)
5. inventory-service   (needs Config Server + Eureka + catalog-service)
6. payment-service     (needs Config Server + Eureka)
7. order-service       (needs Config Server + Eureka + inventory-service + payment-service)
```

Each service is started independently (no root Gradle build):

```bash
cd registry-service && ./gradlew bootRun
cd config-server    && ./gradlew bootRun
# ... and so on
```

---

## API Usage Examples

All requests go through the gateway on port `8080`.

**List all products:**
```bash
curl http://localhost:8080/api/catalog
```

**Filter by category and brand:**
```bash
curl "http://localhost:8080/api/catalog?category=Men%27s+Fashion&brand=Adidas"
```

**Check inventory:**
```bash
curl http://localhost:8080/api/inventory/1
```

**Place an order:**
```bash
curl -X POST http://localhost:8084/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productId": 1}'
```

> Note: order-service and payment-service routes are not yet exposed through the gateway. Call them directly on their ports for now.

---

## Package Structure

All services follow the same base package pattern:

```
com.aleksa.<service_name>/
├── <ServiceName>Application.java
├── controller/
├── service/
├── model/
├── repository/      (catalog-service only)
└── client/          (inventory-service, order-service)
```

---

## Inter-Service Communication

| Caller | Callee | Client | Endpoint |
|---|---|---|---|
| inventory-service | catalog-service | Feign | `GET /api/catalog` |
| order-service | inventory-service | Feign | `GET /api/inventory/{productId}` |
| order-service | payment-service | Feign | `POST /api/payments/process` |

---

## Notes

- No Docker support is currently present. Each service must be started manually.
- Catalog data is loaded from CSV (no database). Inventory data is randomized in memory - it resets on every restart.
- Payment outcomes are randomized - there is no real payment logic.
