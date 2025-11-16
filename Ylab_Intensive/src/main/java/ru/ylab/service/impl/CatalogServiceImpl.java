package ru.ylab.service.impl;


import ru.ylab.model.Product;
import ru.ylab.model.enums.Action;
import ru.ylab.repository.ProductRepository;
import ru.ylab.service.AuditService;
import ru.ylab.service.CacheService;
import ru.ylab.service.CatalogService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CatalogServiceImpl implements CatalogService {
    private final ProductRepository repository;
    private final AuditService auditService;
    private final CacheService cacheService;
    private String currentUser;


    public CatalogServiceImpl(ProductRepository repository, AuditService auditService, CacheService cacheService) {
        this.repository = repository;
        this.auditService = auditService;
        this.cacheService = cacheService;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }


    // CREATE
    @Override
    public Product addProduct(String name, String category, String brand,
                              BigDecimal price, String description) {

        validateProductData(name, category, brand, price);

        Product product = new Product(name, category, brand, price, description);
        Product added = repository.add(product);

        // Очистка кэша после изменений
        cacheService.invalidateAll();

        // Аудит
        auditService.log(currentUser, Action.ADD_PRODUCT,
                "Added product: " + added.getName() + " (ID: " + added.getId() + ")");

        return added;
    }

    // READ
    @Override
    public List<Product> getAllProducts() {
        String cacheKey = "all_products";

        // Проверяем кэш
        List<Product> cached = cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // Загружаем из репозитория
        List<Product> products = repository.findAll();
        cacheService.put(cacheKey, products);

        return products; // Возвращаем копии
    }

    public Optional<Product> getProductById(int id) {
        return repository.findById(id);
    }

    // UPDATE
    @Override
    public boolean updateProduct(int id, String name, String category,
                                 String brand, BigDecimal price, String description) {
        validateProductData(name, category, brand, price);

        Product updated = new Product(name, category, brand, price, description);
        boolean success = repository.update(id, updated);

        if (success) {
            cacheService.invalidateAll();
            auditService.log(currentUser, Action.UPDATE_PRODUCT,
                    "Updated product ID: " + id);
        }

        return success;
    }

    // DELETE
    @Override
    public boolean deleteProduct(int id) {
        Optional<Product> product = repository.findById(id);
        if (product.isEmpty()) {
            return false;
        }

        boolean success = repository.delete(id);

        if (success) {
            cacheService.invalidateAll();
            auditService.log(currentUser, Action.DELETE_PRODUCT,
                    "Deleted product: " + product.get().getName() + " (ID: " + id + ")");
        }

        return success;
    }

    // SEARCH & FILTER
    @Override
    public List<Product> searchByName(String keyword) {
        String cacheKey = "search_name_" + keyword;

        List<Product> cached = cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Product> results = repository.searchByName(keyword);
        cacheService.put(cacheKey, results);

        auditService.log(currentUser, Action.SEARCH_PRODUCT, "Searched by name: " + keyword);
        return results;
    }

    @Override
    public List<Product> filterByCategory(String category) {
        String cacheKey = "filter_category_" + category;

        List<Product> cached = cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Product> results = repository.findByCategory(category);
        cacheService.put(cacheKey, results);

        return results;
    }

    @Override
    public List<Product> filterByBrand(String brand) {
        String cacheKey = "filter_brand_" + brand;

        List<Product> cached = cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Product> results = repository.findByBrand(brand);
        cacheService.put(cacheKey, results);

        return results;
    }

    @Override
    public List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        String cacheKey = String.format("filter_price_%.2f_%.2f", minPrice, maxPrice);

        List<Product> cached = cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Product> results = repository.findByPriceRange(minPrice, maxPrice);
        cacheService.put(cacheKey, results);

        return results;
    }

    // METRICS
    @Override
    public int getTotalProductCount() {
        return repository.count();
    }

    @Override
    public List<String> getAllCategories() {
        return List.copyOf(repository.getAllCategories());
    }

    @Override
    public List<String> getAllBrands() {
        return List.copyOf(repository.getAllBrands());
    }

    // VALIDATION
    private void validateProductData(String name, String category,
                                     String brand, BigDecimal price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand cannot be empty");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }


}

