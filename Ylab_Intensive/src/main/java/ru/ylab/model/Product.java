package ru.ylab.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


public class Product {
    private Integer id;
    private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Product(String name, String category, String brand, BigDecimal price, String description) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
    }

    public Product(Integer id, String name, String category, String brand, BigDecimal price, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
    }


    public Product(Integer id, String name, String category, String brand, BigDecimal price, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', brand='%s', price=%.2f₽}", id, name, category, brand, price);
    }

    public String toDetailedString() {
        return String.format("""
                  ID: %d
                  Название: %s
                  Категория: %s
                  Бренд: %s
                  Цена: %.2f₽
                  Описание: %s
                """, id, name, category, brand, price, description != null ? description : "Нет описания");
    }
}
