package com.aleksa.payment_service.model;

public record PaymentRequest(String orderId, int productId) {}
