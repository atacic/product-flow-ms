package com.aleksa.payment_service.service;

import com.aleksa.payment_service.dto.PaymentRequest;
import com.aleksa.payment_service.dto.PaymentResponse;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {

    private final Random random = new Random();

    public PaymentResponse processPayment(PaymentRequest request) {
        String paymentId = UUID.randomUUID().toString();
        boolean success = random.nextBoolean();
        return new PaymentResponse(paymentId, request.orderId(), success);
    }
}
