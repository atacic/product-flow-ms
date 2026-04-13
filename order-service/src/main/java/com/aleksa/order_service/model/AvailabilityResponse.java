package com.aleksa.order_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AvailabilityResponse(int productId, boolean available) {}
