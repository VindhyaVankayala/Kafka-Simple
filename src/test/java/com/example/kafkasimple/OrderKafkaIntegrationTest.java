package com.example.kafkasimple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

/** Starts a real Kafka broker in Docker, sends a message and checks that the consumer receives it. */
@SpringBootTest
@Testcontainers
class OrderKafkaIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer("apache/kafka:3.8.0");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private OrderConsumer orderConsumer;

    @Test
    void sentOrderIsReceivedByConsumer() {
        OrderEvent order = new OrderEvent("order-1", "Laptop", 2);

        orderProducer.send(order);

        await().atMost(Duration.ofSeconds(20))
                .untilAsserted(() -> assertThat(orderConsumer.getReceivedOrders()).contains(order));
    }
}
