package main.java.ru.ylab.service;

import main.java.ru.ylab.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления каталогом товаров маркетплейса.
 * <p>
 * Предоставляет операции создания, чтения, обновления и удаления товаров (CRUD),
 * а также функции поиска и фильтрации по различным критериям.
 * Все операции записываются в аудит-лог для отслеживания действий пользователей.
 * </p>
 * <p>
 * Сервис использует кэширование для ускорения повторных запросов.
 * При изменении данных кэш автоматически инвалидируется.
 * </p>
 * <p>
 * <strong>Потокобезопасность:</strong> сервис НЕ является потокобезопасным.
 * </p>
 * <p>
 * Пример использования:
 * <pre>{@code
 * CatalogService service = new CatalogServiceImpl(repository, auditService, cacheService);
 * service.setCurrentUser("admin");
 *
 * Product product = service.addProduct(
 *     "MacBook Pro",
 *     "Electronics",
 *     "Apple",
 *     2499.99,
 *     "Professional laptop with M3 chip"
 * );
 *
 * List<Product> electronics = service.filterByCategory("Electronics");
 * Optional<Product> found = service.getProductById(product.getId());
 * }</pre>
 * </p>
 *
 * @see Product
 * @see CacheService
 * @see AuditService
 * @author Ваше Имя
 * @version 1.0
 * @since 2025-11-09
 */
public interface CatalogService {

    /**
     * Устанавливает текущего пользователя для аудита операций.
     * <p>
     * Это имя будет использоваться во всех записях аудита для отслеживания,
     * кто выполнил каждое действие.
     * </p>
     *
     * @param currentUser имя пользователя (может быть {@code null} для системных операций)
     */
    void setCurrentUser(String currentUser);

    /**
     * Добавляет новый товар в каталог.
     * <p>
     * Выполняет валидацию всех полей перед добавлением.
     * При успешном добавлении:
     * <ul>
     *   <li>Генерируется уникальный ID</li>
     *   <li>Очищается весь кэш</li>
     *   <li>Записывается событие ADD в аудит-лог</li>
     * </ul>
     * </p>
     *
     * @param name название товара (не может быть {@code null} или пустым)
     * @param category категория товара (не может быть {@code null} или пустой)
     * @param brand бренд товара (не может быть {@code null} или пустым)
     * @param price цена товара в долларах (должна быть {@code >= 0})
     * @param description описание товара (может быть {@code null})
     * @return добавленный товар с присвоенным ID (никогда не {@code null})
     * @throws IllegalArgumentException если название, категория или бренд пусты
     * @throws IllegalArgumentException если цена отрицательная
     * @see #updateProduct(int, String, String, String, double, String)
     * @see #deleteProduct(int)
     */
    Product addProduct(String name, String category, String brand,
                       double price, String description);

    /**
     * Возвращает все товары из каталога.
     * <p>
     * Результаты кэшируются для повышения производительности.
     * Каждый товар в списке является защитной копией (defensive copy),
     * поэтому изменения объектов не влияют на состояние каталога.
     * </p>
     *
     * @return список всех товаров (может быть пустым, но никогда {@code null})
     * @see #filterByCategory(String)
     * @see #filterByBrand(String)
     */
    List<Product> getAllProducts();

    /**
     * Получает товар по его уникальному идентификатору.
     * <p>
     * <strong>Важно:</strong> возвращается защитная копия товара,
     * изменения которой не влияют на оригинал в репозитории.
     * </p>
     *
     * @param id уникальный идентификатор товара (должен быть положительным)
     * @return {@link Optional} с товаром, если найден; {@link Optional#empty()} в противном случае
     * @see #getAllProducts()
     */
    Optional<Product> getProductById(int id);

    /**
     * Обновляет существующий товар новыми данными.
     * <p>
     * Выполняет валидацию всех полей перед обновлением.
     * При успешном обновлении:
     * <ul>
     *   <li>Кэш инвалидируется</li>
     *   <li>Записывается событие UPDATE в аудит-лог</li>
     *   <li>Обновляется поле modifiedDate товара</li>
     * </ul>
     * </p>
     *
     * @param id идентификатор товара для обновления
     * @param name новое название (не может быть {@code null} или пустым)
     * @param category новая категория (не может быть {@code null} или пустой)
     * @param brand новый бренд (не может быть {@code null} или пустым)
     * @param price новая цена (должна быть {@code >= 0})
     * @param description новое описание (может быть {@code null})
     * @return {@code true} если товар успешно обновлён, {@code false} если товар не найден
     * @throws IllegalArgumentException если какое-либо поле невалидно
     * @see #addProduct(String, String, String, double, String)
     */
    boolean updateProduct(int id, String name, String category, String brand,
                          double price, String description);

    /**
     * Удаляет товар из каталога.
     * <p>
     * При успешном удалении:
     * <ul>
     *   <li>Кэш инвалидируется</li>
     *   <li>Записывается событие DELETE в аудит-лог</li>
     * </ul>
     * </p>
     *
     * @param id идентификатор товара для удаления
     * @return {@code true} если товар успешно удалён, {@code false} если товар не найден
     * @see #addProduct(String, String, String, double, String)
     */
    boolean deleteProduct(int id);

    /**
     * Ищет товары по ключевому слову в названии.
     * <p>
     * Поиск НЕ зависит от регистра (case-insensitive).
     * Результаты кэшируются для повторных запросов с тем же ключевым словом.
     * </p>
     * <p>
     * Примеры:
     * <pre>
     * searchByName("laptop") найдёт "MacBook Pro Laptop", "Dell Laptop"
     * searchByName("PHONE") найдёт "iPhone 15", "Samsung Phone"
     * </pre>
     * </p>
     *
     * @param keyword ключевое слово для поиска (не может быть {@code null} или пустым)
     * @return список найденных товаров (может быть пустым, но никогда {@code null})
     * @throws IllegalArgumentException если keyword {@code null} или пустой
     * @see #filterByCategory(String)
     */
    List<Product> searchByName(String keyword);

    /**
     * Фильтрует товары по категории.
     * <p>
     * Использует индексацию для быстрого поиска O(1).
     * Результаты кэшируются для повторных запросов.
     * </p>
     *
     * @param category название категории для фильтрации (не может быть {@code null})
     * @return список товаров в указанной категории (может быть пустым, но никогда {@code null})
     * @see #getAllCategories()
     * @see #filterByBrand(String)
     */
    List<Product> filterByCategory(String category);

    /**
     * Фильтрует товары по бренду.
     * <p>
     * Использует индексацию для быстрого поиска O(1).
     * Результаты кэшируются для повторных запросов.
     * </p>
     *
     * @param brand название бренда для фильтрации (не может быть {@code null})
     * @return список товаров указанного бренда (может быть пустым, но никогда {@code null})
     * @see #getAllBrands()
     * @see #filterByCategory(String)
     */
    List<Product> filterByBrand(String brand);

    /**
     * Фильтрует товары по диапазону цен.
     * <p>
     * Возвращает товары, цена которых находится в указанном диапазоне (включительно).
     * </p>
     *
     * @param minPrice минимальная цена (должна быть {@code >= 0})
     * @param maxPrice максимальная цена (должна быть {@code >= minPrice})
     * @return список товаров в указанном ценовом диапазоне (может быть пустым, но никогда {@code null})
     * @throws IllegalArgumentException если {@code minPrice > maxPrice} или цены отрицательные
     */
    List<Product> filterByPriceRange(double minPrice, double maxPrice);

    /**
     * Возвращает общее количество товаров в каталоге.
     *
     * @return количество товаров ({@code >= 0})
     * @see #getAllProducts()
     */
    int getTotalProductCount();

    /**
     * Возвращает список всех уникальных категорий товаров.
     * <p>
     * Результаты кэшируются для повышения производительности.
     * </p>
     *
     * @return список категорий (может быть пустым, но никогда {@code null})
     * @see #filterByCategory(String)
     */
    List<String> getAllCategories();

    /**
     * Возвращает список всех уникальных брендов товаров.
     * <p>
     * Результаты кэшируются для повышения производительности.
     * </p>
     *
     * @return список брендов (может быть пустым, но никогда {@code null})
     * @see #filterByBrand(String)
     */
    List<String> getAllBrands();
}