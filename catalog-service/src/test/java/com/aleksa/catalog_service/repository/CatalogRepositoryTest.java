package com.aleksa.catalog_service.repository;

import com.aleksa.catalog_service.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CatalogRepositoryTest {

    private CatalogRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CatalogRepository();
    }

    @Test
    void findAll_returnsAllProducts() {

        // When
        List<Product> products = repository.findAll();

        // Then
        assertThat(products).hasSize(1000);
    }

    @Test
    void findById_existingId_returnsProduct() {

        // When
        Optional<Product> product = repository.findById(1);

        // Then
        assertThat(product).isPresent();
        assertThat(product.get().productId()).isEqualTo(1);
        assertThat(product.get().name()).isEqualTo("Dress");
        assertThat(product.get().brand()).isEqualTo("Adidas");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {

        // When
        Optional<Product> product = repository.findById(99999);

        // Then
        assertThat(product).isEmpty();
    }

    @Test
    void findByCategory_returnsMatchingProducts() {

        // When
        List<Product> products = repository.findByCategory("Men's Fashion");

        // Then
        assertThat(products).isNotEmpty();
        assertThat(products).allMatch(p -> p.category().equalsIgnoreCase("Men's Fashion"));
    }

    @Test
    void findByCategory_unknownCategory_returnsEmpty() {

        // When
        List<Product> products = repository.findByCategory("Unknown Category");

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    void findByBrand_returnsMatchingProducts() {

        // When
        List<Product> products = repository.findByBrand("Zara");

        // Then
        assertThat(products).isNotEmpty();
        assertThat(products).allMatch(p -> p.brand().equalsIgnoreCase("Zara"));
    }

    @Test
    void findByBrand_unknownBrand_returnsEmpty() {

        // When
        List<Product> products = repository.findByBrand("UnknownBrand");

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    void findByCategory_isCaseInsensitive() {

        // When
        List<Product> lower = repository.findByCategory("men's fashion");
        List<Product> upper = repository.findByCategory("MEN'S FASHION");

        // Then
        assertThat(lower).isNotEmpty();
        assertThat(lower).hasSameElementsAs(upper);
    }

    @Test
    void findByBrand_isCaseInsensitive() {

        // When
        List<Product> lower = repository.findByBrand("zara");
        List<Product> upper = repository.findByBrand("ZARA");

        // Then
        assertThat(lower).isNotEmpty();
        assertThat(lower).hasSameElementsAs(upper);
    }

    @Test
    void findByCategoryAndBrand_returnsMatchingProducts() {

        // When
        List<Product> products = repository.findByCategoryAndBrand("Men's Fashion", "Adidas");

        // Then
        assertThat(products).isNotEmpty();
        assertThat(products).allMatch(p ->
                p.category().equalsIgnoreCase("Men's Fashion") && p.brand().equalsIgnoreCase("Adidas"));
    }

    @Test
    void findByCategoryAndBrand_returnsEmpty_whenNoMatch() {

        // When
        List<Product> products = repository.findByCategoryAndBrand("Men's Fashion", "UnknownBrand");

        // Then
        assertThat(products).isEmpty();
    }
}
