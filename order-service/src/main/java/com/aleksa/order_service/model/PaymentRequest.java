package com.aleksa.order_service.model;

public record PaymentRequest(String orderId, int productId) {}
