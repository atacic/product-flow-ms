package com.aleksa.order_service.controller;

import com.aleksa.order_service.model.OrderRequest;
import com.aleksa.order_service.model.OrderResponse;
import com.aleksa.order_service.model.OrderStatus;
import com.aleksa.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(service)).build();
    }

    @Test
    void shouldCreateOrderAndReturnConfirmed() throws Exception {

        // Given
        OrderResponse response = new OrderResponse("order-123", 1, OrderStatus.CONFIRMED);
        when(service.createOrder(any(OrderRequest.class))).thenReturn(response);

        // When
        var result = mockMvc.perform(post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OrderRequest(1))));

        // Then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("order-123"))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(service).createOrder(any(OrderRequest.class));
    }

    @Test
    void shouldCreateOrderAndReturnRejectedUnavailable() throws Exception {

        // Given
        OrderResponse response = new OrderResponse("order-456", 1, OrderStatus.REJECTED_UNAVAILABLE);
        when(service.createOrder(any(OrderRequest.class))).thenReturn(response);

        // When
        var result = mockMvc.perform(post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OrderRequest(1))));

        // Then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REJECTED_UNAVAILABLE"));
    }

    @Test
    void shouldCreateOrderAndReturnRejectedPaymentFailed() throws Exception {

        // Given
        OrderResponse response = new OrderResponse("order-789", 1, OrderStatus.REJECTED_PAYMENT_FAILED);
        when(service.createOrder(any(OrderRequest.class))).thenReturn(response);

        // When
        var result = mockMvc.perform(post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OrderRequest(1))));

        // Then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REJECTED_PAYMENT_FAILED"));
    }
}
