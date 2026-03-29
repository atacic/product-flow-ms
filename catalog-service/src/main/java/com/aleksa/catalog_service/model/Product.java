package com.aleksa.catalog_service.model;

public record Product(int productId, String name, String brand, String category,
                      double price, double rating, String color, String size) {}
