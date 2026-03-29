package com.aleksa.catalog_service.controller;

import com.aleksa.catalog_service.model.Product;
import com.aleksa.catalog_service.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll(@RequestParam(required = false) String category, @RequestParam(required = false) String brand) {
        if (category != null && brand != null) {
            return ResponseEntity.ok(service.getByCategoryAndBrand(category, brand));
        }
        if (category != null) {
            return ResponseEntity.ok(service.getByCategory(category));
        }
        if (brand != null) {
            return ResponseEntity.ok(service.getByBrand(brand));
        }
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable int id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
