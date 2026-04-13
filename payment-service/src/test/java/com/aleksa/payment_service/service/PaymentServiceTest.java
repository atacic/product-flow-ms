package com.aleksa.payment_service.service;

import com.aleksa.payment_service.model.PaymentRequest;
import com.aleksa.payment_service.model.PaymentResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentServiceTest {

    private final PaymentService service = new PaymentService();

    @Test
    void processPayment_returnsResponseWithOrderId() {

        // Given
        PaymentRequest request = new PaymentRequest("order-123", 1);

        // When
        PaymentResponse response = service.processPayment(request);

        // Then
        assertThat(response.orderId()).isEqualTo("order-123");
        assertThat(response.paymentId()).isNotBlank();
    }

    @Test
    void processPayment_returnsUniquePaymentIds() {

        // Given
        PaymentRequest request = new PaymentRequest("order-123", 1);

        // When
        PaymentResponse first = service.processPayment(request);
        PaymentResponse second = service.processPayment(request);

        // Then
        assertThat(first.paymentId()).isNotEqualTo(second.paymentId());
    }

    @Test
    void processPayment_successFieldIsBoolean() {

        // Given
        PaymentRequest request = new PaymentRequest("order-123", 1);

        // When
        PaymentResponse response = service.processPayment(request);

        // Then
        assertThat(response.success()).isInstanceOf(Boolean.class);
    }
}
