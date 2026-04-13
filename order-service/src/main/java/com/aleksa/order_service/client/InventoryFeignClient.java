package com.aleksa.order_service.client;

import com.aleksa.order_service.model.AvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface InventoryFeignClient {

    @GetMapping("/api/inventory/{productId}")
    AvailabilityResponse getAvailability(@PathVariable int productId);
}
