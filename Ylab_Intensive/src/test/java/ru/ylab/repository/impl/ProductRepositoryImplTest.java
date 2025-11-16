package ru.ylab.repository.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ylab.config.DatabaseConfig;
import ru.ylab.config.LiquibaseConfig;
import ru.ylab.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для ProductRepositoryImpl с использованием Testcontainers
 * 
 * Тестирует CRUD операции, поиск и фильтрацию товаров в БД PostgreSQL
 */
@Testcontainers
@DisplayName("ProductRepositoryImpl Integration Tests")
class ProductRepositoryImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test_catalog_db")
        .withUsername("test_user")
        .withPassword("test_pass");

    private static ProductRepositoryImpl repository;

    @BeforeAll
    static void setup() {
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());
        LiquibaseConfig.runMigrations();
        repository = new ProductRepositoryImpl();
    }

    @BeforeEach
    void cleanDatabase() {
        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("SET session_replication_role = replica");
            stmt.execute("DELETE FROM catalog.products");
            stmt.execute("SET session_replication_role = default");

        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database", e);
        }
    }
    @Test
    @DisplayName("Should add product and return generated ID")
    void testAddProduct() {
        // Given
        Product product = new Product(
            "Laptop",
            "Electronics",
            "Dell",
            new BigDecimal("999.99"),
            "High-performance laptop"
        );

        // When
        Product added = repository.add(product);

        // Then
        assertNotNull(added, "Товар не должен быть null");
        assertNotNull(added.getId(), "ID товара должен быть сгенерирован");
        assertEquals("Laptop", added.getName(), "Название должно совпадать");
        assertEquals(new BigDecimal("999.99"), added.getPrice(), "Цена должна совпадать");
        assertNotNull(added.getCreatedAt(), "createdAt должен быть установлен");
    }

    @Test
    @DisplayName("Should find product by ID")
    void testFindById() {
        // Given
        Product product = new Product(
            "Mouse",
            "Peripherals",
            "Logitech",
            new BigDecimal("29.99"),
            "Wireless mouse"
        );
        Product added = repository.add(product);

        // When
        Optional<Product> found = repository.findById(added.getId());

        // Then
        assertTrue(found.isPresent(), "Товар должен быть найден");
        assertEquals("Mouse", found.get().getName(), "Название должно совпадать");
        assertEquals(new BigDecimal("29.99"), found.get().getPrice(), "Цена должна совпадать");
    }

    @Test
    @DisplayName("Should return empty Optional for non-existent product ID")
    void testFindByIdNotFound() {
        // When
        Optional<Product> found = repository.findById(999999);

        // Then
        assertFalse(found.isPresent(), "Optional должен быть пустой");
    }

    @Test
    @DisplayName("Should update product")
    void testUpdateProduct() {
        // Given
        Product product = new Product(
            "Keyboard",
            "Peripherals",
            "Corsair",
            new BigDecimal("150.00"),
            "Mechanical keyboard"
        );
        Product added = repository.add(product);
        
        Product updatedProduct = new Product(
            added.getId(),
            "Mechanical Keyboard",
            "Peripherals",
            "Corsair",
            new BigDecimal("149.99"),
            "Updated mechanical keyboard",
            added.getCreatedAt(),
            null
        );

        // When
        boolean isUpdated = repository.update(added.getId(), updatedProduct);
        Optional<Product> result = repository.findById(added.getId());

        // Then
        assertTrue(isUpdated, "Функция вернула false");
        assertTrue(result.isPresent(), "обновленый продукт не найден");
        assertEquals("Mechanical Keyboard", result.get().getName(), "Название должно быть обновлено");
        assertEquals(new BigDecimal("149.99"), result.get().getPrice(), "Цена должна быть обновлена");
    }

    @Test
    @DisplayName("Should delete product")
    void testDeleteProduct() {
        // Given
        Product product = new Product(
            "USB Cable",
            "Accessories",
            "Anker",
            new BigDecimal("12.99"),
            "3-meter USB-C cable"
        );
        Product added = repository.add(product);

        // When
        boolean deleted = repository.delete(added.getId());

        // Then
        assertTrue(deleted, "Удаление должно быть успешным");
        
        Optional<Product> found = repository.findById(added.getId());
        assertFalse(found.isPresent(), "Товар не должен быть найден после удаления");
    }

    @Test
    @DisplayName("Should find all products")
    void testFindAll() {
        // Given
        repository.add(new Product("Product1", "Cat1", "Brand1", new BigDecimal("10.00"), "Desc1"));
        repository.add(new Product("Product2", "Cat2", "Brand2", new BigDecimal("20.00"), "Desc2"));
        repository.add(new Product("Product3", "Cat3", "Brand3", new BigDecimal("30.00"), "Desc3"));

        // When
        List<Product> products = repository.findAll();

        // Then
        assertNotNull(products, "Список не должен быть null");
        assertTrue(products.size() >= 3, "Должно быть минимум 3 товара");
    }

    @Test
    @DisplayName("Should find products by category")
    void testFindByCategory() {
        // Given
        String category = "Electronics";
        repository.add(new Product("Laptop", category, "Brand1", new BigDecimal("999.99"), "Desc"));
        repository.add(new Product("Tablet", category, "Brand2", new BigDecimal("499.99"), "Desc"));
        repository.add(new Product("Mouse", "Peripherals", "Brand3", new BigDecimal("29.99"), "Desc"));

        // When
        List<Product> found = repository.findByCategory(category);

        // Then
        assertNotNull(found, "Список не должен быть null");
        assertTrue(found.size() >= 2, "Должно быть минимум 2 товара в категории");
        assertTrue(found.stream().allMatch(p -> p.getCategory().equals(category)), 
            "Все товары должны быть из нужной категории");
    }

    @Test
    @DisplayName("Should return empty list for non-existent category")
    void testFindByCategoryNotFound() {
        // When
        List<Product> found = repository.findByCategory("NonExistentCategory123");

        // Then
        assertNotNull(found, "Список не должен быть null");
        assertTrue(found.isEmpty(), "Список должен быть пустой");
    }

    @Test
    @DisplayName("Should find products by brand")
    void testFindByBrand() {
        // Given
        String brand = "Dell";
        repository.add(new Product("Laptop1", "Electronics", brand, new BigDecimal("999.99"), "Desc"));
        repository.add(new Product("Laptop2", "Electronics", brand, new BigDecimal("1299.99"), "Desc"));
        repository.add(new Product("Laptop3", "Electronics", "HP", new BigDecimal("899.99"), "Desc"));

        // When
        List<Product> found = repository.findByBrand(brand);

        // Then
        assertNotNull(found, "Список не должен быть null");
        assertTrue(found.size() >= 2, "Должно быть минимум 2 товара бренда");
        assertTrue(found.stream().allMatch(p -> p.getBrand().equals(brand)), 
            "Все товары должны быть нужного бренда");
    }

    @Test
    @DisplayName("Should search products by name")
    void testSearchByName() {
        // Given
        repository.add(new Product("iPhone 15 Pro", "Phones", "Apple", new BigDecimal("999.99"), "Latest iPhone"));
        repository.add(new Product("iPhone 15", "Phones", "Apple", new BigDecimal("799.99"), "iPhone"));
        repository.add(new Product("Samsung Galaxy", "Phones", "Samsung", new BigDecimal("899.99"), "Galaxy"));

        // When
        List<Product> found = repository.searchByName("iPhone");

        // Then
        assertNotNull(found, "Список не должен быть null");
        assertTrue(found.size() >= 2, "Должно быть найдено минимум 2 товара");
        assertTrue(found.stream().allMatch(p -> p.getName().toUpperCase().contains("IPHONE")), 
            "Все найденные товары должны содержать 'iPhone'");
    }

    @Test
    @DisplayName("Should return empty list for non-matching search")
    void testSearchByNameNotFound() {
        // When
        List<Product> found = repository.searchByName("XYZ123NonExistent");

        // Then
        assertNotNull(found, "Список не должен быть null");
        assertTrue(found.isEmpty(), "Список должен быть пустой");
    }

    @Test
    @DisplayName("Should find products by price range")
    void testFindByPriceRange() {
        // Given
        repository.add(new Product("Cheap", "Cat", "Brand1", new BigDecimal("10.00"), "Desc"));
        repository.add(new Product("Medium", "Cat", "Brand2", new BigDecimal("50.00"), "Desc"));
        repository.add(new Product("Expensive", "Cat", "Brand3", new BigDecimal("100.00"), "Desc"));

        // When
        List<Product> found = repository.findByPriceRange(
            new BigDecimal("30.00"),
            new BigDecimal("80.00")
        );

        // Then
        assertNotNull(found, "Список не должен быть null");
        assertTrue(found.size() >= 1, "Должно быть минимум 1 товар в диапазоне цен");
        assertTrue(found.stream().allMatch(
            p -> p.getPrice().compareTo(new BigDecimal("30.00")) >= 0 &&
                 p.getPrice().compareTo(new BigDecimal("80.00")) <= 0
        ), "Все товары должны быть в диапазоне цен");
    }

    @Test
    @DisplayName("Should correctly use BigDecimal for price precision")
    void testBigDecimalPricePrecision() {
        // Given
        BigDecimal price = new BigDecimal("99.99");
        Product product = new Product(
            "Test Product",
            "Test",
            "TestBrand",
            price,
            "Testing precision"
        );
        
        // When
        Product added = repository.add(product);
        Optional<Product> found = repository.findById(added.getId());

        // Then
        assertTrue(found.isPresent(), "Товар должен быть найден");
        assertEquals(price, found.get().getPrice(), "Цена должна совпадать с точностью");
        assertEquals(0, price.compareTo(found.get().getPrice()), 
            "BigDecimal сравнение должно быть нулевым");
    }

    @Test
    @DisplayName("Should store and retrieve timestamps")
    void testTimestamps() {
        // Given
        Product product = new Product(
            "Timestamp Test",
            "Test",
            "Brand",
            new BigDecimal("10.00"),
            "Testing timestamps"
        );
        
        // When
        Product added = repository.add(product);
        Optional<Product> found = repository.findById(added.getId());

        // Then
        assertTrue(found.isPresent(), "Товар должен быть найден");
        assertNotNull(found.get().getCreatedAt(), "createdAt должен быть установлен");
        assertNotNull(found.get().getUpdatedAt(), "updatedAt должен быть установлен");
    }

    @Test
    @DisplayName("Should handle null category and brand")
    void testNullCategoryAndBrand() {
        // Given
        Product product = new Product(
            "Generic Product",
            null,  // null category
            null,  // null brand
            new BigDecimal("50.00"),
            "Product with null category and brand"
        );
        
        // When
        Product added = repository.add(product);
        Optional<Product> found = repository.findById(added.getId());

        // Then
        assertTrue(found.isPresent(), "Товар должен быть найден");
        assertNull(found.get().getCategory(), "Category может быть null");
        assertNull(found.get().getBrand(), "Brand может быть null");
    }

    @Test
    @DisplayName("Should handle long product description")
    void testLongDescription() {
        // Given
        String longDesc = "This is a very long product description. " +
            "It contains multiple sentences and lots of information about the product. " +
            "The description is meant to test that the database can handle longer text strings. " +
            "It should be stored and retrieved correctly without any data loss or truncation.";
        
        Product product = new Product(
            "Complex Product",
            "Category",
            "Brand",
            new BigDecimal("100.00"),
            longDesc
        );
        
        // When
        Product added = repository.add(product);
        Optional<Product> found = repository.findById(added.getId());

        // Then
        assertTrue(found.isPresent(), "Товар должен быть найден");
        assertEquals(longDesc, found.get().getDescription(), "Длинное описание должно совпадать");
    }
}
