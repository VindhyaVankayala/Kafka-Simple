package com.example.kafkasimple;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderProducer orderProducer;

    @MockBean
    private OrderConsumer orderConsumer;

    @Test
    void validOrderIsSentToProducer() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId": "order-1", "product": "Laptop", "quantity": 2}
                                """))
                .andExpect(status().isAccepted());

        verify(orderProducer).send(new OrderEvent("order-1", "Laptop", 2));
    }

    @Test
    void invalidOrderIsRejected() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId": "", "product": "Laptop", "quantity": 0}
                                """))
                .andExpect(status().isBadRequest());
    }
}
