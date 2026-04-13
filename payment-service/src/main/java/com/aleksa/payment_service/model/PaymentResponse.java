package com.aleksa.payment_service.model;

public record PaymentResponse(String paymentId, String orderId, boolean success) {}
