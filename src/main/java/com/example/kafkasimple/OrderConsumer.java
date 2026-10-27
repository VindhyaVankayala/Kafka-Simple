package com.example.kafkasimple;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/** Receives order messages from Kafka and keeps them in memory so they can be viewed via the REST API. */
@Service
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    private final List<OrderEvent> receivedOrders = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = KafkaTopicConfig.ORDERS_TOPIC, groupId = "order-consumer")
    public void receive(OrderEvent order) {
        log.info("Received order {}: {} x {}", order.orderId(), order.quantity(), order.product());
        receivedOrders.add(order);
    }

    public List<OrderEvent> getReceivedOrders() {
        return List.copyOf(receivedOrders);
    }
}
