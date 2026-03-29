package com.aleksa.catalog_service.controller;

import com.aleksa.catalog_service.model.Product;
import com.aleksa.catalog_service.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    @Mock
    private CatalogService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CatalogController(service)).build();
    }

    @Test
    void shouldReturnAllProductsWhenNoParamsProvided() throws Exception {

        // Given
        Product product = new Product(1, "Dress", "Adidas", "Men's Fashion", 40.0, 1.04, "Black", "XL");
        when(service.getAll()).thenReturn(List.of(product));

        // When
        var result = mockMvc.perform(get("/api/catalog"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].name").value("Dress"))
                .andExpect(jsonPath("$[0].brand").value("Adidas"));

        verify(service).getAll();
    }

    @Test
    void shouldReturnProductsFilteredByCategory() throws Exception {

        // Given
        Product product = new Product(1, "Dress", "Adidas", "Men's Fashion", 40.0, 1.04, "Black", "XL");
        when(service.getByCategory("Men's Fashion")).thenReturn(List.of(product));

        // When
        var result = mockMvc.perform(get("/api/catalog").param("category", "Men's Fashion"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("Men's Fashion"));

        verify(service).getByCategory("Men's Fashion");
    }

    @Test
    void shouldReturnProductsFilteredByBrand() throws Exception {

        // Given
        Product product = new Product(1, "Dress", "Adidas", "Men's Fashion", 40.0, 1.04, "Black", "XL");
        when(service.getByBrand("Adidas")).thenReturn(List.of(product));

        // When
        var result = mockMvc.perform(get("/api/catalog").param("brand", "Adidas"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Adidas"));

        verify(service).getByBrand("Adidas");
    }

    @Test
    void shouldReturnEmptyListWhenNoCategoryMatches() throws Exception {

        // Given
        when(service.getByCategory("Unknown")).thenReturn(List.of());

        // When
        var result = mockMvc.perform(get("/api/catalog").param("category", "Unknown"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service).getByCategory("Unknown");
    }

    @Test
    void shouldReturnProductWhenIdExists() throws Exception {

        // Given
        Product product = new Product(1, "Dress", "Adidas", "Men's Fashion", 40.0, 1.04, "Black", "XL");
        when(service.getById(1)).thenReturn(Optional.of(product));

        // When
        var result = mockMvc.perform(get("/api/catalog/{id}", 1));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Dress"))
                .andExpect(jsonPath("$.price").value(40.0));

        verify(service).getById(1);
    }

    @Test
    void shouldReturnProductsFilteredByCategoryAndBrand() throws Exception {

        // Given
        Product match = new Product(1, "Dress", "Adidas", "Men's Fashion", 40.0, 1.04, "Black", "XL");
        when(service.getByCategoryAndBrand("Men's Fashion", "Adidas")).thenReturn(List.of(match));

        // When
        var result = mockMvc.perform(get("/api/catalog")
                .param("category", "Men's Fashion")
                .param("brand", "Adidas"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].brand").value("Adidas"));

        verify(service).getByCategoryAndBrand("Men's Fashion", "Adidas");
    }

    @Test
    void shouldReturn404WhenProductIdDoesNotExist() throws Exception {

        // Given
        when(service.getById(99999)).thenReturn(Optional.empty());

        // When
        var result = mockMvc.perform(get("/api/catalog/{id}", 99999));

        // Then
        result.andExpect(status().isNotFound());

        verify(service).getById(99999);
    }
}
