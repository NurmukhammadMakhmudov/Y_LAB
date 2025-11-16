package ru.ylab.repository;

import ru.ylab.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Интерфейс репозитория для работы с товарами
 *
 * Определяет все операции для работы с товарами в каталоге:
 * - CRUD операции (создание, чтение, обновление, удаление)
 * - Поиск по различным критериям
 * - Фильтрацию по категориям, брендам, цене
 * - Получение общей информации о каталоге
 *
 * Все операции выполняются через PostgreSQL с помощью JDBC.
 * ID товаров генерируются автоматически с помощью SEQUENCE.
 *
 * @author Y.Lab
 * @author Makhmudov Nurmukhammad
 * @version 2.0
 */
public interface ProductRepository {

    /**
     * Добавить новый товар в каталог
     * ID генерируется автоматически базой данных через SEQUENCE
     *
     * @param product Объект товара без ID (ID будет присвоен БД)
     * @return Товар с назначенным ID
     */
    Product add(Product product);

    /**
     * Найти товар по ID
     *
     * @param id Идентификатор товара
     * @return Optional с товаром если найден, Optional.empty() если не найден
     */
    Optional<Product> findById(int id);

    /**
     * Получить все товары из каталога
     *
     * @return Список всех товаров, отсортированный по ID
     */
    List<Product> findAll();

    /**
     * Обновить информацию о товаре
     *
     * @param id ID товара для обновления
     * @param updatedProduct Объект с новыми данными
     * @return true если обновление успешно, false если товар не найден
     */
    boolean update(int id, Product updatedProduct);

    /**
     * Удалить товар из каталога
     *
     * @param id ID товара для удаления
     * @return true если удаление успешно, false если товар не найден
     */
    boolean delete(int id);

    /**
     * Найти товары по категории
     *
     * @param category Название категории
     * @return Список товаров данной категории
     */
    List<Product> findByCategory(String category);

    /**
     * Найти товары по бренду
     *
     * @param brand Название бренда
     * @return Список товаров данного бренда
     */
    List<Product> findByBrand(String brand);

    /**
     * Поиск товаров по названию
     * Поиск выполняется без учета регистра (case-insensitive)
     * Использует частичное совпадение (LIKE в SQL)
     *
     * @param keyword Ключевое слово для поиска
     * @return Список товаров с совпадающими названиями
     */
    List<Product> searchByName(String keyword);

    /**
     * Найти товары в диапазоне цен
     *
     * @param lowerBound Минимальная цена (включительно)
     * @param upperBound Максимальная цена (включительно)
     * @return Список товаров в указанном диапазоне цен
     */
    List<Product> findByPriceRange(BigDecimal lowerBound, BigDecimal upperBound);

    /**
     * Получить количество товаров в каталоге
     *
     * @return Общее количество товаров
     */
    int count();

    /**
     * Получить все уникальные категории в каталоге
     *
     * @return Набор (Set) названий всех категорий
     */
    Set<String> getAllCategories();

    /**
     * Получить все уникальные бренды в каталоге
     *
     * @return Набор (Set) названий всех брендов
     */
    Set<String> getAllBrands();
}
