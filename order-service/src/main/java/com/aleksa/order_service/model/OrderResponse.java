package com.aleksa.order_service.model;

public record OrderResponse(String orderId, int productId, OrderStatus status) {}
