package com.aleksa.inventory_service.service;

import com.aleksa.inventory_service.client.CatalogFeignClient;
import com.aleksa.inventory_service.model.AvailabilityResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryService {

    private final CatalogFeignClient catalogFeignClient;
    private final Map<Integer, Boolean> inventory = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public InventoryService(CatalogFeignClient catalogFeignClient) {
        this.catalogFeignClient = catalogFeignClient;
    }

    @PostConstruct
    void initialize() {
        catalogFeignClient.getAllProducts().forEach(p -> inventory.put(p.productId(), random.nextBoolean()));
    }

    public Optional<AvailabilityResponse> getAvailability(int productId) {
        return Optional.ofNullable(inventory.get(productId))
                .map(available -> new AvailabilityResponse(productId, available));
    }
}
