package ru.ylab.repository.impl;

import ru.ylab.config.DatabaseConfig;
import ru.ylab.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ylab.repository.ProductRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class ProductRepositoryImpl implements ProductRepository {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryImpl.class);

    // CREATE
    public Product add(Product product) {
        String sql = "INSERT INTO catalog.products (name, category, brand, price, description) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id, name, category, brand, price, description, created_at, updated_at";

        if (product == null) {
            throw new IllegalArgumentException("Product is null");
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setString(3, product.getBrand());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setString(5, product.getDescription());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product newProduct = mapResultSetToProduct(rs);
                    logger.info("Product added: {} (ID: {})", newProduct.getName(), newProduct.getId());
                    return newProduct;
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding product: {}", product.getName(), e);
            throw new RuntimeException("Failed to add product", e);
        }

        throw new RuntimeException("Failed to add product: no result returned");
    }

    // READ
    public Optional<Product> findById(int id) {
        String sql = "SELECT id, name, category, brand, price, description, created_at, updated_at FROM catalog.products WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding product by ID: {}", id, e);
            throw new RuntimeException("Failed to find product", e);
        }

        return Optional.empty();
    }

    public List<Product> findAll() {
        String sql = "SELECT id, name, category, brand, price, description,created_at, updated_at FROM catalog.products ORDER BY id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all products", e);
            throw new RuntimeException("Failed to fetch products", e);
        }

        return products;
    }

    // UPDATE
    public boolean update(int id, Product updatedProduct) {
        String sql = "UPDATE catalog.products SET name = ?, category = ?, brand = ?, price = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, updatedProduct.getName());
            stmt.setString(2, updatedProduct.getCategory());
            stmt.setString(3, updatedProduct.getBrand());
            stmt.setBigDecimal(4, updatedProduct.getPrice());
            stmt.setString(5, updatedProduct.getDescription());
            stmt.setInt(6, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Product updated: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating product: {}", id, e);
            throw new RuntimeException("Failed to update product", e);
        }

        return false;
    }



    // DELETE
    public boolean delete(int id) {
        String sql = "DELETE FROM catalog.products WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Product deleted: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting product: {}", id, e);
            throw new RuntimeException("Failed to delete product", e);
        }

        return false;
    }

    // SEARCH && FILTER
    public List<Product> findByCategory(String category) {
        String sql = "SELECT id, name, category, brand, price, description, created_at, updated_at FROM catalog.products WHERE category = ?";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding products by category: {}", category, e);
            throw new RuntimeException("Failed to find products", e);
        }

        return products;
    }

    public List<Product> findByBrand(String brand) {
        String sql = "SELECT id, name, category, brand, price, description, created_at, updated_at FROM catalog.products WHERE brand = ?";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, brand);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding products by brand: {}", brand, e);
            throw new RuntimeException("Failed to find products", e);
        }

        return products;
    }

    public List<Product> searchByName(String keyword) {
        String sql = "SELECT id, name, category, brand, price, description, created_at, updated_at FROM catalog.products WHERE LOWER(name) LIKE LOWER(?)";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching products by name: {}", keyword, e);
            throw new RuntimeException("Failed to search products", e);
        }

        return products;
    }

    @Override
    public List<Product> findByPriceRange(BigDecimal lowerBound, BigDecimal upperBound) {
        String sql = "SELECT id, name, category, brand, price, description, created_at, updated_at FROM catalog.products WHERE price BETWEEN ? AND ?";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, lowerBound);
            stmt.setBigDecimal(2, upperBound);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding products in price range: {} - {}", lowerBound, upperBound, e);
            throw new RuntimeException("Failed to find products", e);
        }

        return products;
    }

    // UTILITY
    public int count() {
        String sql = "SELECT COUNT(*) FROM catalog.products";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting products", e);
            throw new RuntimeException("Failed to count products", e);
        }

        return 0;
    }

    public Set<String> getAllCategories() {
        String sql = "SELECT DISTINCT category FROM catalog.products ORDER BY category";
        Set<String> categories = new HashSet<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            logger.error("Error fetching categories", e);
            throw new RuntimeException("Failed to fetch categories", e);
        }

        return categories;
    }

    public Set<String> getAllBrands() {
        String sql = "SELECT DISTINCT brand FROM catalog.products ORDER BY brand";
        Set<String> brands = new HashSet<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                brands.add(rs.getString("brand"));
            }
        } catch (SQLException e) {
            logger.error("Error fetching brands", e);
            throw new RuntimeException("Failed to fetch brands", e);
        }

        return brands;
    }

    // Helper method
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("brand"),
                rs.getBigDecimal("price"),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
