# Kafka Simple Demo

A minimal **Spring Boot + Apache Kafka** application that sends and receives messages.

```
POST /api/orders ──▶ OrderProducer ──▶ [ Kafka topic "orders" ] ──▶ OrderConsumer ──▶ GET /api/orders/received
```

1. You send an order to the REST API.
2. `OrderProducer` publishes it to the Kafka topic `orders` as JSON (using `KafkaTemplate`).
3. `OrderConsumer` listens to the topic (`@KafkaListener`), logs the order and stores it in memory.
4. `GET /api/orders/received` shows everything the consumer has received.

## Project structure

| Class | What it does |
|---|---|
| `OrderEvent` | The message (a Java record) — converted to/from JSON automatically |
| `KafkaTopicConfig` | Creates the `orders` topic with 3 partitions on start-up |
| `OrderProducer` | Sends messages with `KafkaTemplate`, using `orderId` as the message key |
| `OrderConsumer` | Receives messages with `@KafkaListener` in consumer group `order-consumer` |
| `OrderController` | REST endpoints to send orders and view received ones |
| `application.yml` | Kafka connection and JSON serializer/deserializer settings |

## Tech stack

Java 21 · Spring Boot 3.3 · Spring for Apache Kafka · Apache Kafka 3.8 (KRaft) · JUnit 5 · Mockito · Testcontainers · Docker Compose · Maven

## Running it

Requirements: Java 21 and Docker.

```bash
docker compose up -d          # starts Kafka and Kafka UI
mvn spring-boot:run           # or run KafkaSimpleDemoApplication from IntelliJ
```

## Try it

```bash
curl -X POST localhost:8080/api/orders -H 'Content-Type: application/json' \
     -d '{"orderId":"order-1","product":"Laptop","quantity":2}'

curl localhost:8080/api/orders/received
```

In the application logs you will see both sides:

```
Sent order order-1 to partition 1 at offset 0
Received order order-1: 2 x Laptop
```

Open **Kafka UI** at http://localhost:8090 → Topics → `orders` → Messages to see the message stored in Kafka.

## Tests

```bash
mvn test
```

- `OrderControllerTest` — web layer test with MockMvc and Mockito (valid and invalid requests).
- `OrderKafkaIntegrationTest` — starts a **real Kafka broker in Docker with Testcontainers**, sends a message
  and checks that the consumer receives it.
