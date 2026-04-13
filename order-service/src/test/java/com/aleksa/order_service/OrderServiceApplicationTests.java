package com.aleksa.order_service;

import com.aleksa.order_service.client.InventoryFeignClient;
import com.aleksa.order_service.client.PaymentFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class OrderServiceApplicationTests {

    @MockitoBean
    InventoryFeignClient inventoryFeignClient;

    @MockitoBean
    PaymentFeignClient paymentFeignClient;

    @Test
    void contextLoads() {
    }
}
