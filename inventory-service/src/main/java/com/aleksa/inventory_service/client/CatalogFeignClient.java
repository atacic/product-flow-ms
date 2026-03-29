package com.aleksa.inventory_service.client;

import com.aleksa.inventory_service.model.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "catalog-service")
public interface CatalogFeignClient {

    @GetMapping("/api/catalog")
    List<ProductDto> getAllProducts();
}
