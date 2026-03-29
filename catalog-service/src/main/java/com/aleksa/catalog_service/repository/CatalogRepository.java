package com.aleksa.catalog_service.repository;

import com.aleksa.catalog_service.model.Product;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class CatalogRepository {

    private final Map<Integer, Product> products;

    public CatalogRepository() {
        this.products = loadFromCsv();
    }

    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }

    public List<Product> findByCategory(String category) {
        return products.values().stream()
                .filter(p -> p.category().equalsIgnoreCase(category))
                .toList();
    }

    public List<Product> findByBrand(String brand) {
        return products.values().stream()
                .filter(p -> p.brand().equalsIgnoreCase(brand))
                .toList();
    }

    public List<Product> findByCategoryAndBrand(String category, String brand) {
        return products.values().stream()
                .filter(p -> p.category().equalsIgnoreCase(category) && p.brand().equalsIgnoreCase(brand))
                .toList();
    }

    private Map<Integer, Product> loadFromCsv() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("data/fashion_products.csv").getInputStream()))) {
            return reader.lines()
                    .skip(1)
                    .map(this::parseLine)
                    .collect(Collectors.toMap(Product::productId, Function.identity()));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load fashion_products.csv", e);
        }
    }

    private Product parseLine(String line) {
        String[] parts = line.split(",");
        return new Product(
                Integer.parseInt(parts[1].trim()),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                Double.parseDouble(parts[5].trim()),
                Double.parseDouble(parts[6].trim()),
                parts[7].trim(),
                parts[8].trim()
        );
    }
}
