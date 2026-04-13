package com.aleksa.order_service.service;

import com.aleksa.order_service.client.InventoryFeignClient;
import com.aleksa.order_service.client.PaymentFeignClient;
import com.aleksa.order_service.model.*;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private InventoryFeignClient inventoryClient;

    @Mock
    private PaymentFeignClient paymentClient;

    @InjectMocks
    private OrderService service;

    @Test
    void createOrder_productAvailableAndPaymentSuccess_returnsConfirmed() {

        // Given
        when(inventoryClient.getAvailability(1)).thenReturn(new AvailabilityResponse(1, true));
        when(paymentClient.processPayment(any())).thenReturn(new PaymentResponse("pay-1", true));

        // When
        OrderResponse result = service.createOrder(new OrderRequest(1));

        // Then
        assertThat(result.productId()).isEqualTo(1);
        assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(result.orderId()).isNotBlank();
        verify(inventoryClient).getAvailability(1);
        verify(paymentClient).processPayment(any());
    }

    @Test
    void createOrder_productNotAvailable_returnsRejectedUnavailable() {

        // Given
        when(inventoryClient.getAvailability(1)).thenReturn(new AvailabilityResponse(1, false));

        // When
        OrderResponse result = service.createOrder(new OrderRequest(1));

        // Then
        assertThat(result.status()).isEqualTo(OrderStatus.REJECTED_UNAVAILABLE);
        verifyNoInteractions(paymentClient);
    }

    @Test
    void createOrder_productNotFound_returnsRejectedUnavailable() {

        // Given
        Request dummyRequest = Request.create(Request.HttpMethod.GET, "/api/inventory/99999",
                Map.of(), null, null, null);
        when(inventoryClient.getAvailability(99999))
                .thenThrow(new FeignException.NotFound("Not found", dummyRequest, null, null));

        // When
        OrderResponse result = service.createOrder(new OrderRequest(99999));

        // Then
        assertThat(result.status()).isEqualTo(OrderStatus.REJECTED_UNAVAILABLE);
        verifyNoInteractions(paymentClient);
    }

    @Test
    void createOrder_productAvailableButPaymentFailed_returnsRejectedPaymentFailed() {

        // Given
        when(inventoryClient.getAvailability(1)).thenReturn(new AvailabilityResponse(1, true));
        when(paymentClient.processPayment(any())).thenReturn(new PaymentResponse("pay-1", false));

        // When
        OrderResponse result = service.createOrder(new OrderRequest(1));

        // Then
        assertThat(result.status()).isEqualTo(OrderStatus.REJECTED_PAYMENT_FAILED);
        verify(paymentClient).processPayment(any());
    }
}
