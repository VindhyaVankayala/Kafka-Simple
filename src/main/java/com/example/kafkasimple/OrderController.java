package com.example.kafkasimple;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProducer orderProducer;
    private final OrderConsumer orderConsumer;

    public OrderController(OrderProducer orderProducer, OrderConsumer orderConsumer) {
        this.orderProducer = orderProducer;
        this.orderConsumer = orderConsumer;
    }

    /** Sends the order to Kafka. */
    @PostMapping
    public ResponseEntity<String> sendOrder(@Valid @RequestBody OrderEvent order) {
        orderProducer.send(order);
        return ResponseEntity.accepted().body("Order " + order.orderId() + " sent to Kafka");
    }

    /** Lists the orders the consumer has received from Kafka. */
    @GetMapping("/received")
    public List<OrderEvent> getReceivedOrders() {
        return orderConsumer.getReceivedOrders();
    }
}
