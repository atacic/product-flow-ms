package com.aleksa.inventory_service.controller;

import com.aleksa.inventory_service.model.AvailabilityResponse;
import com.aleksa.inventory_service.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new InventoryController(service)).build();
    }

    @Test
    void shouldReturnAvailabilityWhenProductExists() throws Exception {

        // Given
        when(service.getAvailability(1)).thenReturn(Optional.of(new AvailabilityResponse(1, true)));

        // When
        var result = mockMvc.perform(get("/api/inventory/{productId}", 1));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.available").value(true));

        verify(service).getAvailability(1);
    }

    @Test
    void shouldReturnUnavailableWhenProductExistsButNotAvailable() throws Exception {

        // Given
        when(service.getAvailability(2)).thenReturn(Optional.of(new AvailabilityResponse(2, false)));

        // When
        var result = mockMvc.perform(get("/api/inventory/{productId}", 2));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(2))
                .andExpect(jsonPath("$.available").value(false));

        verify(service).getAvailability(2);
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {

        // Given
        when(service.getAvailability(99999)).thenReturn(Optional.empty());

        // When
        var result = mockMvc.perform(get("/api/inventory/{productId}", 99999));

        // Then
        result.andExpect(status().isNotFound());

        verify(service).getAvailability(99999);
    }
}
