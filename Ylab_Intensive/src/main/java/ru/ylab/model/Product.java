package main.java.ru.ylab.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private final String name;
    private final String category;
    private final String brand;
    private double price;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public Product(String name, String category, String brand, double price, String description) {
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
        setModifiedDate();
        setCreatedDate();
    }

    public Product(int id, String name, String category, String brand, double price, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.description = description;
        setModifiedDate();
        setCreatedDate();
    }

    public int getId() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        setModifiedDate();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        setModifiedDate();
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public Product copy() {
        Product copied =  new Product(this.id, this.name, this.category,
                this.brand, this.price, this.description);

        copied.createdDate = this.createdDate;
        copied.modifiedDate = this.modifiedDate;
        return copied;

    }
    private void setModifiedDate() {
        if (modifiedDate == null) {
            modifiedDate = LocalDateTime.now();
        } else if (modifiedDate.isBefore(LocalDateTime.now())) {
            modifiedDate = LocalDateTime.now();
        }
    }

    private void setCreatedDate() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name) && Objects.equals(category, product.category) && Objects.equals(brand, product.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category, brand);
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', brand='%s', price=%.2f}",
                id, name, category, brand, price);
    }
}
