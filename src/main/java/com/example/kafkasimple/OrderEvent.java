package com.example.kafkasimple;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/** The message we send through Kafka. It is converted to JSON automatically. */
public record OrderEvent(
        @NotBlank String orderId,
        @NotBlank String product,
        @Positive int quantity) {
}
