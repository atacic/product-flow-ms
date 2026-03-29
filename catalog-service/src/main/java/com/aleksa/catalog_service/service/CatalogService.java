package com.aleksa.catalog_service.service;

import com.aleksa.catalog_service.model.Product;
import com.aleksa.catalog_service.repository.CatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    private final CatalogRepository repository;

    public CatalogService(CatalogRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public Optional<Product> getById(int id) {
        return repository.findById(id);
    }

    public List<Product> getByCategory(String category) {
        return repository.findByCategory(category);
    }

    public List<Product> getByBrand(String brand) {
        return repository.findByBrand(brand);
    }

    public List<Product> getByCategoryAndBrand(String category, String brand) {
        return repository.findByCategoryAndBrand(category, brand);
    }
}
