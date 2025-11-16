package main.java.ru.ylab.repository;

import main.java.ru.ylab.model.AppData;
import main.java.ru.ylab.model.Product;

import java.util.*;

public class ProductRepository {
    private final Map<Integer, Product> products; // основное хранилище
    private final Map<String, Set<Integer>> categoryIndex; // индекс по категориям
    private final Map<String, Set<Integer>> brandIndex; // индекс по брендам
    private final AppData appData;


    public ProductRepository(AppData appData) {
        this.appData = appData;
        this.products = appData.getProducts();
        this.categoryIndex = new  HashMap<>();
        this.brandIndex = new  HashMap<>();

        rebuildIndexes();
    }

    //CREATE

    public Product add(Product product) {
        int productId = appData.getNextProductId();
        appData.setNextProductId(productId + 1);

        Product newProduct = new Product(productId,
                product.getName(),
                product.getCategory(),
                product.getBrand(),
                product.getPrice(),
                product.getDescription());
        products.put(productId, newProduct);
        addToIndexes(newProduct);
        return newProduct;
    }

    // READ

    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }

    public List<Product> findAll() {
        return products.values().stream()
                .toList();
    }


    // UPDATE

    public boolean update(int id, Product updatedProduct) {
        Product oldProduct = products.get(id);
        if (oldProduct == null) {
            return false;
        }

        removeFromIndexes(id, oldProduct);

        Product update = new Product(id,
                updatedProduct.getName(),
                updatedProduct.getCategory(),
                updatedProduct.getBrand(),
                updatedProduct.getPrice(),
                updatedProduct.getDescription());

        products.put(id, update);

        addToIndexes(update);

        return true;
    }

    // DELETE

    public boolean delete(int id) {
        Product product = products.remove(id);
        if (product == null) {
            return false;
        }
        removeFromIndexes(id, product);

        return true;
    }

    // SEARCH && FILTER

    public List<Product> findByCategory(String category) {
       Set<Integer> productSet = categoryIndex.getOrDefault(category, Collections.emptySet());

        return productSet.stream().map(products::get).toList();
    }

    public List<Product> findByBrand(String brand) {
        Set<Integer> productSet = brandIndex.getOrDefault(brand, Collections.emptySet());
        return productSet.stream().map(products::get).toList();
    }

    public List<Product> searchByName(String keyword) {
        return products.values().stream()
                .filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    public List<Product> findByPriceRange(double lowerBound, double upperBound) {
        return products.values().stream()
                .filter(product ->  product.getPrice() >= lowerBound && product.getPrice() <= upperBound)
                .toList();
    }


    // UTILITY

    public int count() {
        return products.size();
    }

    public Set<String> getAllCategories() {
        return new HashSet<>(categoryIndex.keySet());
    }

    public Set<String> getAllBrands() {
        return new HashSet<>(brandIndex.keySet());
    }

    private void addToIndexes(Product product) {
        categoryIndex.computeIfAbsent(product.getCategory(), k -> new HashSet<>()).add(product.getId());
        brandIndex.computeIfAbsent(product.getBrand(), k -> new HashSet<>()).add(product.getId());
    }

    private void removeFromIndexes(int id, Product product) {
        Set<Integer> categoryIds = categoryIndex.get(product.getCategory());
        if (categoryIds != null) {
            categoryIds.remove(id);
            if (categoryIds.isEmpty()) {
                categoryIndex.remove(product.getCategory());
            }
        }

        Set<Integer> brandIds = brandIndex.get(product.getBrand());
        if (brandIds != null) {
            brandIds.remove(id);
            if (brandIds.isEmpty()) {
                brandIndex.remove(product.getBrand());
            }
        }
    }

    /**
     * Восстанавливает индексы из загруженных данных
     */
    private void rebuildIndexes() {
        for (Product product : products.values()) {
            addToIndexes(product);
        }
    }

}
