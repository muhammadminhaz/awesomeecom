# AwesomeEcom - Microservices E-commerce Platform

AwesomeEcom is a robust, scalable, and modern e-commerce backend application built using a Microservices architecture with Spring Boot. It leverages industry-standard tools for service discovery, load balancing, messaging, monitoring, and container orchestration.

## 🏗 Architecture

The project is composed of several loosely coupled microservices, communicating synchronously via REST and asynchronously via Apache Kafka.

### Core Components
*   **API Gateway (`api-gateway`)**: The single entry point for all client requests. It handles routing, load balancing, and rate limiting.
*   **Service Discovery (`eureka-server`)**: Acts as a registry where all microservices register themselves, enabling dynamic service discovery.
*   **Microservices**:
    *   **Customer Service**: Manages user profiles and authentication.
    *   **Product Service**: Manages product catalog and inventory.
    *   **Cart Service**: Handles user shopping carts.
    *   **Order Service**: Manages order placement and processing.
    *   **Payment Service**: Handles payment processing (integrated with Stripe).
    *   **Notification Service**: Sends emails/notifications (triggered via Kafka).

### Infrastructure & Data
*   **Databases**: Isolated **PostgreSQL** instances for Customer, Product, Cart, and Order services to ensure data sovereignty.
*   **Caching & Rate Limiting**: **Redis** is used for caching in services and for distributed rate limiting in the API Gateway.
*   **Messaging**: **Apache Kafka** is used for event-driven communication (e.g., Order Placed -> Send Notification).

### Observability
*   **Monitoring**: **Prometheus** scrapes metrics from services; **Grafana** visualizes them.
*   **Logging**: **ELK Stack** (Elasticsearch, Logstash, Kibana) for centralized log aggregation and analysis.

---

## 🛠 Tech Stack

*   **Language**: Java
*   **Framework**: Spring Boot, Spring Cloud (Gateway, Netflix Eureka, OpenFeign)
*   **Databases**: PostgreSQL
*   **Message Broker**: Apache Kafka
*   **Cache**: Redis
*   **Containerization**: Docker, Docker Compose
*   **Orchestration**: Kubernetes (Minikube)
*   **Monitoring**: Prometheus, Grafana
*   **Logging**: Elasticsearch, Logstash, Kibana

---

## 🚀 API Endpoints & Routing

All requests should be directed to the **API Gateway** (Port `4006`). The gateway routes traffic to the appropriate microservice using **Client-Side Load Balancing** (via Eureka).

| Service | Route Path | Description |
| :--- | :--- | :--- |
| **Customer Service** | `/customer/**` | User registration, login, profile management |
| **Product Service** | `/products/**` | Product listing, details, inventory |
| **Cart Service** | `/cart/**` | Add/remove items, view cart |
| **Order Service** | `/orders/**` | Place orders, view order history |
| **Payment Service** | `/payment/**` | Process payments |
| **Notification Service** | `/notify/**` | Notification triggers (mostly internal) |

### Load Balancing & Rate Limiting
*   **Load Balancing**: The API Gateway uses `lb://` URIs to dynamically resolve service instances from the Eureka registry, distributing traffic evenly across available instances.
*   **Rate Limiting**: Implemented using Redis. The gateway limits the number of requests per second/minute from a specific IP to prevent abuse.

### 🛒 Smart Cart Caching Strategy
The **Cart Service** implements a Write-Behind caching strategy to optimize database performance:
*   **Redis First**: When items are added to the cart, they are stored in **Redis** with a 60-minute TTL (Time-To-Live).
*   **Delayed Persistence**: A scheduler (`CartExpiryScheduler`) persists the cart data from Redis to the **PostgreSQL** database only after the 60-minute window expires or when specific triggers occur.
*   **Benefit**: This significantly reduces write pressure on the database by avoiding continuous DB writes for every single item addition or update.

---

## 🐳 Deployment: Docker Compose

The easiest way to run the entire stack locally is using Docker Compose.

1.  **Prerequisites**: Docker and Docker Compose installed.
2.  **Run**:
    ```bash
    docker-compose up -d --build
    ```
3.  **Access**:
    *   API Gateway: `http://localhost:4006`
    *   Eureka Dashboard: `http://localhost:8761`
    *   Grafana: `http://localhost:3000` (Creds: admin/admin)
    *   Kibana: `http://localhost:5601`

---

## ☸️ Deployment: Kubernetes (Minikube)

You can deploy the entire application stack to a local Minikube cluster using the provided automation script.

### ⚡️ One-Click Deployment Script
The project includes a **PowerShell script (`k8s-deployment.ps1`)** that automates the entire deployment process. This script handles:
1.  Starting Minikube.
2.  Configuring Docker to use Minikube's environment.
3.  Building Docker images for all microservices.
4.  Applying all Kubernetes manifests (PVCs, ConfigMaps, Secrets, Databases, Redis, Kafka, and Services).
5.  Waiting for pods to be ready.
6.  Port-forwarding services for local access.

**How to run:**
```powershell
./k8s-deployment.ps1
```

### Manual Deployment
If you prefer manual `kubectl` commands, apply the manifests in the `k8s/` directory in the following order:
1.  Namespace (`awesomeecom`)
2.  PVCs (`pvc-deployment.yaml`)
3.  Config/Secrets (`k8s/env`)
4.  Infrastructure (`k8s/db`, `k8s/redis`, `k8s/kafka`)
5.  Microservices (`k8s/services`)

---

## 📊 Monitoring & Logging

*   **Prometheus**: Collects metrics from all Spring Boot applications (exposed via `/actuator/prometheus`).
*   **Grafana**: Connects to Prometheus to display dashboards for CPU, Memory, Request Latency, etc.
*   **ELK Stack**: Logstash collects logs, sends them to Elasticsearch, and Kibana provides a UI to search and analyze logs.
