package com.aleksa.inventory_service.service;

import com.aleksa.inventory_service.client.CatalogFeignClient;
import com.aleksa.inventory_service.model.AvailabilityResponse;
import com.aleksa.inventory_service.model.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private CatalogFeignClient catalogFeignClient;

    private InventoryService service;

    @BeforeEach
    void setUp() {
        when(catalogFeignClient.getAllProducts()).thenReturn(List.of(
                new ProductDto(1),
                new ProductDto(2)
        ));
        service = new InventoryService(catalogFeignClient);
        service.initialize();
    }

    @Test
    void initialize_fetchesAllProductsFromCatalog() {

        // Then
        verify(catalogFeignClient).getAllProducts();
    }

    @Test
    void getAvailability_existingProduct_returnsAvailabilityResponse() {

        // When
        Optional<AvailabilityResponse> result = service.getAvailability(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().productId()).isEqualTo(1);
        assertThat(result.get().available()).isInstanceOf(Boolean.class);
    }

    @Test
    void getAvailability_unknownProduct_returnsEmpty() {

        // When
        Optional<AvailabilityResponse> result = service.getAvailability(99999);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getAvailability_returnsStableAvailability() {

        // When
        Optional<AvailabilityResponse> first = service.getAvailability(1);
        Optional<AvailabilityResponse> second = service.getAvailability(1);

        // Then
        assertThat(first).isPresent();
        assertThat(second).isPresent();
        assertThat(first.get().available()).isEqualTo(second.get().available());
    }
}
