package com.aleksa.payment_service.dto;

public record PaymentResponse(String paymentId, String orderId, boolean success) {}
