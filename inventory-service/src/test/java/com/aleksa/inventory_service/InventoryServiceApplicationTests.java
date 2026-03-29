package com.aleksa.inventory_service;

import com.aleksa.inventory_service.client.CatalogFeignClient;
import com.aleksa.inventory_service.model.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
class InventoryServiceApplicationTests {

    @MockitoBean
    CatalogFeignClient catalogFeignClient;

    @Test
    void contextLoads() {
        when(catalogFeignClient.getAllProducts()).thenReturn(List.of(new ProductDto(1)));
    }
}
