package com.aleksa.payment_service.controller;

import com.aleksa.payment_service.dto.PaymentRequest;
import com.aleksa.payment_service.dto.PaymentResponse;
import com.aleksa.payment_service.service.PaymentService;
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
class PaymentControllerTest {

    @Mock
    private PaymentService service;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PaymentController(service)).build();
    }

    @Test
    void shouldProcessPaymentAndReturnResponse() throws Exception {

        // Given
        PaymentRequest request = new PaymentRequest("order-123", 1);
        PaymentResponse response = new PaymentResponse("payment-abc", "order-123", true);
        when(service.processPayment(any(PaymentRequest.class))).thenReturn(response);

        // When
        var result = mockMvc.perform(post("/api/payment/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("payment-abc"))
                .andExpect(jsonPath("$.orderId").value("order-123"))
                .andExpect(jsonPath("$.success").value(true));

        verify(service).processPayment(any(PaymentRequest.class));
    }
}
