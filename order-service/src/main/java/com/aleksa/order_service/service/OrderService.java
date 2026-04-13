package com.aleksa.order_service.service;

import com.aleksa.order_service.client.InventoryFeignClient;
import com.aleksa.order_service.client.PaymentFeignClient;
import com.aleksa.order_service.model.*;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private final InventoryFeignClient inventoryClient;
    private final PaymentFeignClient paymentClient;

    public OrderService(InventoryFeignClient inventoryClient, PaymentFeignClient paymentClient) {
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
    }

    public OrderResponse createOrder(OrderRequest request) {
        String orderId = UUID.randomUUID().toString();

        AvailabilityResponse availability;
        try {
            availability = inventoryClient.getAvailability(request.productId());
        } catch (FeignException.NotFound e) {
            return new OrderResponse(orderId, request.productId(), OrderStatus.REJECTED_UNAVAILABLE);
        }

        if (!availability.available()) {
            return new OrderResponse(orderId, request.productId(), OrderStatus.REJECTED_UNAVAILABLE);
        }

        PaymentResponse payment = paymentClient.processPayment(new PaymentRequest(orderId, request.productId()));

        OrderStatus status = payment.success() ? OrderStatus.CONFIRMED : OrderStatus.REJECTED_PAYMENT_FAILED;
        return new OrderResponse(orderId, request.productId(), status);
    }
}
