package com.aleksa.catalog_service.service;

import com.aleksa.catalog_service.model.Product;
import com.aleksa.catalog_service.repository.CatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private CatalogRepository repository;

    @InjectMocks
    private CatalogService service;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(1, "Dress", "Adidas", "Men's Fashion", 40.0, 1.04, "Black", "XL");
    }

    @Test
    void getAll_delegatesToRepository() {

        // Given
        when(repository.findAll()).thenReturn(List.of(sampleProduct));

        // When
        List<Product> result = service.getAll();

        // Then
        assertThat(result).containsExactly(sampleProduct);
        verify(repository).findAll();
    }

    @Test
    void getById_existingId_returnsProduct() {

        // Given
        when(repository.findById(1)).thenReturn(Optional.of(sampleProduct));

        // When
        Optional<Product> result = service.getById(1);

        // Then
        assertThat(result).contains(sampleProduct);
        verify(repository).findById(1);
    }

    @Test
    void getById_nonExistingId_returnsEmpty() {

        // Given
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = service.getById(999);

        // Then
        assertThat(result).isEmpty();
        verify(repository).findById(999);
    }

    @Test
    void getByCategory_delegatesToRepository() {

        // Given
        when(repository.findByCategory("Men's Fashion")).thenReturn(List.of(sampleProduct));

        // When
        List<Product> result = service.getByCategory("Men's Fashion");

        // Then
        assertThat(result).containsExactly(sampleProduct);
        verify(repository).findByCategory("Men's Fashion");
    }

    @Test
    void getByBrand_delegatesToRepository() {

        // Given
        when(repository.findByBrand("Adidas")).thenReturn(List.of(sampleProduct));

        // When
        List<Product> result = service.getByBrand("Adidas");

        // Then
        assertThat(result).containsExactly(sampleProduct);
        verify(repository).findByBrand("Adidas");
    }

    @Test
    void getByCategoryAndBrand_delegatesToRepository() {

        // Given
        when(repository.findByCategoryAndBrand("Men's Fashion", "Adidas")).thenReturn(List.of(sampleProduct));

        // When
        List<Product> result = service.getByCategoryAndBrand("Men's Fashion", "Adidas");

        // Then
        assertThat(result).containsExactly(sampleProduct);
        verify(repository).findByCategoryAndBrand("Men's Fashion", "Adidas");
    }
}
