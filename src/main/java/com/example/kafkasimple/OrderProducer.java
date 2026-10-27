package com.example.kafkasimple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/** Sends order messages to Kafka. */
@Service
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(OrderEvent order) {
        // The orderId is the message key: messages with the same key always go to the same partition.
        kafkaTemplate.send(KafkaTopicConfig.ORDERS_TOPIC, order.orderId(), order)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send order {}", order.orderId(), ex);
                    } else {
                        log.info("Sent order {} to partition {} at offset {}", order.orderId(),
                                result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }
}
