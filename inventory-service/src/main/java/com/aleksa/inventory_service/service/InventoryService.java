package com.aleksa.inventory_service.service;

import com.aleksa.inventory_service.client.CatalogFeignClient;
import com.aleksa.inventory_service.model.AvailabilityResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class InventoryService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final CatalogFeignClient catalogFeignClient;
    private final Map<Integer, Boolean> inventory = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public InventoryService(CatalogFeignClient catalogFeignClient) {
        this.catalogFeignClient = catalogFeignClient;
    }

    @Retry(name = "inventory-retry")
    @CircuitBreaker(name = "inventory-service-cb", fallbackMethod = "initializeFallback")
    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {

        logger.info("Inventory initializing...");

        if (!inventory.isEmpty()) {
            return;
        }
        catalogFeignClient.getAllProducts().forEach(p -> inventory.put(p.productId(), random.nextBoolean()));

        logger.info("Inventory initialized with " + inventory.size() + " products");
    }

    public void initializeFallback(Throwable t) {
        logger.warning("Circuit breaker fallback triggered for inventory initialization. Service unavailable: " + t.getMessage());
    }

    public Optional<AvailabilityResponse> getAvailability(int productId) {
        return Optional.ofNullable(inventory.get(productId))
                .map(available -> new AvailabilityResponse(productId, available));
    }
}
