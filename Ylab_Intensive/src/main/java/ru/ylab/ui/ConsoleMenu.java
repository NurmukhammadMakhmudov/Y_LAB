package main.java.ru.ylab.ui;


import main.java.ru.ylab.model.AuditRecord;
import main.java.ru.ylab.model.Product;
import main.java.ru.ylab.model.enums.Action;
import main.java.ru.ylab.service.AuditService;
import main.java.ru.ylab.service.CacheService;
import main.java.ru.ylab.service.CatalogService;
import main.java.ru.ylab.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


/**
 * Главный класс консольного меню приложения.
 * Управляет пользовательским интерфейсом и обработкой команд.
 * Рефакторинг: методы разбиты на логические части:
 * - Экраны меню: showMainMenu(), showAdminMenu(), showProductMenu()
 * - Обработка выбора: handleMainChoice(), handleAdminChoice()
 * - Операции с товарами: addNewProduct(), editProduct(), deleteProductByUser()
 * - Поиск и фильтрация: searchAndDisplayProducts(), filterProductsByCategory()
 * - Чтение ввода: readInt(), readDouble(), readString()
 * - Вывод данных: printProductTable(), printProductDetails(), printMetrics()
 */
public class ConsoleMenu {

    private final CatalogService catalogService;
    private final UserService userService;
    private final AuditService auditService;
    private final Scanner scanner;
    private final CacheService cacheService;
    private String currentUser = null;

    public ConsoleMenu(CatalogService catalogService, UserService userService,
                       AuditService auditService, Scanner scanner, CacheService cacheService) {
        this.catalogService = catalogService;
        this.userService = userService;
        this.auditService = auditService;
        this.scanner = scanner;
        this.cacheService = cacheService;
    }

    /**
     * Главный цикл приложения: авторизация и основное меню.
     */
    public void start() {
        while (true) {
            if (currentUser == null) {
                if (!showLoginMenu()) {
                    break;
                }
            } else {
                if (!showMainMenu()) {
                    break;
                }
            }
        }
    }

    // ==================== ЭКРАНЫ МЕНЮ ====================

    /**
     * Экран авторизации и регистрации.
     */
    private boolean showLoginMenu() {
        printLoginHeader();
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("0. Выход");

        int choice = readInt("Выбор: ");

        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleRegistration();
            case 0 -> {
                System.out.println("До свидания!");
                return false;
            }
            default -> System.out.println("Неверный выбор!");
        }
        return true;
    }

    /**
     * Главное меню.
     */
    private boolean showMainMenu() {
        printMainMenuHeader();

        System.out.println("1. Просмотр всех товаров");
        System.out.println("2. Поиск товара");
        System.out.println("3. Добавить товар");
        System.out.println("4. Редактировать товар");
        System.out.println("5. Удалить товар");
        System.out.println("6. Фильтрация товаров");
        System.out.println("7. Метрики приложения");
        System.out.println("8. История действий");
        System.out.println("9. Выход");

        int choice = readInt("Выбор: ");
        return handleMainMenuChoice(choice);
    }

    /**
     * Обработка выбора в главном меню.
     */
    private boolean handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1 -> displayAllProducts();
            case 2 -> searchAndDisplayProducts();
            case 3 -> addNewProduct();
            case 4 -> editProductMenu();
            case 5 -> deleteProductMenu();
            case 6 -> showFilterMenu();
            case 7 -> showMetricsMenu();
            case 8 -> displayAuditHistory();
            case 9 -> {
                System.out.println("До свидания, " + currentUser + "!");
                currentUser = null;
                return true;
            }
            default -> System.out.println("Неверный выбор!");
        }
        return true;
    }

    /**
     * Меню фильтрации товаров.
     */
    private void showFilterMenu() {
        System.out.println("\n========== ФИЛЬТРАЦИЯ ==========");
        System.out.println("1. По категории");
        System.out.println("2. По бренду");
        System.out.println("3. По цене");
        System.out.println("0. Назад");

        int choice = readInt("Выбор: ");

        switch (choice) {
            case 1 -> filterProductsByCategory();
            case 2 -> filterProductsByBrand();
            case 3 -> filterProductsByPrice();
            case 0 -> { /* вернёмся в главное меню */ }
            default -> System.out.println("Неверный выбор!");
        }
    }

    /**
     * Меню метрик и статистики.
     */
    private void showMetricsMenu() {
        System.out.println("\n========== МЕТРИКИ ==========");
        System.out.println("Всего товаров: " + catalogService.getTotalProductCount());
        System.out.println("Категорий: " + catalogService.getAllCategories().size());
        System.out.println("Брендов: " + catalogService.getAllBrands().size());
        printCacheMetrics();
    }

    // ==================== ОПЕРАЦИИ С АВТОРИЗАЦИЕЙ ====================

    /**
     * Обработка входа пользователя.
     */
    private void handleLogin() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        if (userService.authenticate(username, password)) {
            currentUser = username;
            auditService.log(username, Action.LOGIN, "Пользователь вошел");
            System.out.println("Вход успешен!");
        } else {
            System.out.println("Неверный логин или пароль!");
        }
    }

    /**
     * Обработка регистрации пользователя.
     */
    private void handleRegistration() {
        System.out.print("Новый логин: ");
        String username = scanner.nextLine().trim();
        System.out.print("Пароль (минимум 4 символа): ");
        String password = scanner.nextLine();

        try {
            userService.register(username, password);
            currentUser = username;
            auditService.log(username, Action.LOGIN, "Новый пользователь зарегистрирован");
            System.out.println("Регистрация успешна!");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка регистрации: " + e.getMessage());
        }
    }

    // ==================== ОПЕРАЦИИ С ТОВАРАМИ ====================

    /**
     * Просмотр всех товаров.
     */
    private void displayAllProducts() {
        List<Product> products = catalogService.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("Товаров не найдено.");
            return;
        }
        printProductTable(products);
        auditService.log(currentUser, Action.SEARCH, "Просмотр всех товаров. Результат: " + products.size() + " товаров");
    }

    /**
     * Добавление нового товара.
     */
    private void addNewProduct() {
        System.out.println("\n========== ДОБАВИТЬ ТОВАР ==========");

        String name = readString("Название: ");
        String category = readString("Категория: ");
        String brand = readString("Бренд: ");
        double price = readDouble("Цена: ");
        System.out.print("Описание: ");
        String description = scanner.nextLine().trim();

        try {
            catalogService.setCurrentUser(currentUser);
            Product product = catalogService.addProduct(name, category, brand, price, description);
            System.out.println("Товар добавлен! ID: " + product.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Меню редактирования товара.
     */
    private void editProductMenu() {
        int id = readInt("Введите ID товара: ");
        Optional<Product> product = catalogService.getProductById(id);

        if (product.isEmpty()) {
            System.out.println("Товар не найден.");
            return;
        }

        printProductDetails(product.get());
        System.out.println("\nВведите новые данные (или Enter для пропуска):");
        editProduct(id);
    }

    /**
     * Редактирование товара (вспомогательный метод).
     */
    private void editProduct(int id) {
        System.out.print("Новое название: ");
        String name = scanner.nextLine().trim();

        System.out.print("Новая категория: ");
        String category = scanner.nextLine().trim();

        System.out.print("Новый бренд: ");
        String brand = scanner.nextLine().trim();

        double price = -1;
        System.out.print("Новая цена: ");
        String priceStr = scanner.nextLine().trim();
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                System.out.println("Неверная цена!");
                return;
            }
        }

        System.out.print("Новое описание: ");
        String description = scanner.nextLine().trim();

        // Если хоть что-то заполнено — обновляем
        if (!name.isEmpty() || !category.isEmpty() || !brand.isEmpty() || price >= 0 || !description.isEmpty()) {
            catalogService.setCurrentUser(currentUser);
            boolean updated = catalogService.updateProduct(id, name, category, brand, price, description);
            if (updated) {
                System.out.println("Товар обновлен!");
            } else {
                System.out.println("Ошибка обновления!");
            }
        }
    }

    /**
     * Меню удаления товара.
     */
    private void deleteProductMenu() {
        int id = readInt("Введите ID товара для удаления: ");
        Optional<Product> product = catalogService.getProductById(id);

        if (product.isEmpty()) {
            System.out.println("Товар не найден.");
            return;
        }

        printProductDetails(product.get());
        if (confirmAction()) {
            catalogService.setCurrentUser(currentUser);
            if (catalogService.deleteProduct(id)) {
                System.out.println("Товар удален!");
            } else {
                System.out.println("Ошибка удаления!");
            }
        }
    }

    // ==================== ПОИСК И ФИЛЬТРАЦИЯ ====================

    /**
     * Поиск товаров по названию.
     */
    private void searchAndDisplayProducts() {
        String keyword = readString("Ключевое слово для поиска: ");
        catalogService.setCurrentUser(currentUser);
        List<Product> results = catalogService.searchByName(keyword);

        if (results.isEmpty()) {
            System.out.println("Результатов не найдено.");
        } else {
            System.out.println("\nНайдено " + results.size() + " товаров:");
            printProductTable(results);
        }
        auditService.log(currentUser, Action.SEARCH, "Поиск по ключевому слову: " + keyword + ". Результат: " + results.size());
    }

    /**
     * Фильтр по категории.
     */
    private void filterProductsByCategory() {
        List<String> categories = catalogService.getAllCategories();
        System.out.println("\nДоступные категории:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i));
        }

        int idx = readInt("Выберите категорию: ") - 1;
        if (idx >= 0 && idx < categories.size()) {
            String category = categories.get(idx);
            catalogService.setCurrentUser(currentUser);
            List<Product> filtered = catalogService.filterByCategory(category);

            if (filtered.isEmpty()) {
                System.out.println("Товаров в категории " + category + " не найдено.");
            } else {
                System.out.println("\nТовары в категории '" + category + "':");
                printProductTable(filtered);
            }
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    /**
     * Фильтр по бренду.
     */
    private void filterProductsByBrand() {
        List<String> brands = catalogService.getAllBrands();
        System.out.println("\nДоступные бренды:");
        for (int i = 0; i < brands.size(); i++) {
            System.out.println((i + 1) + ". " + brands.get(i));
        }

        int idx = readInt("Выберите бренд: ") - 1;
        if (idx >= 0 && idx < brands.size()) {
            String brand = brands.get(idx);
            catalogService.setCurrentUser(currentUser);
            List<Product> filtered = catalogService.filterByBrand(brand);

            if (filtered.isEmpty()) {
                System.out.println("Товаров бренда " + brand + " не найдено.");
            } else {
                System.out.println("\nТовары бренда '" + brand + "':");
                printProductTable(filtered);
            }
        } else {
            System.out.println("Неверный выбор!");
        }
    }

    /**
     * Фильтр по цене.
     */
    private void filterProductsByPrice() {
        double min = readDouble("Минимальная цена: ");
        double max = readDouble("Максимальная цена: ");

        catalogService.setCurrentUser(currentUser);
        List<Product> filtered = catalogService.filterByPriceRange(min, max);

        if (filtered.isEmpty()) {
            System.out.println("Товаров в диапазоне цен не найдено.");
        } else {
            System.out.println("\nТовары в диапазоне цен [" + min + " - " + max + "]:");
            printProductTable(filtered);
        }
    }

    /**
     * Вывод истории аудита.
     */
    private void displayAuditHistory() {
        List<AuditRecord> records = auditService.getAllRecords();

        if (records.isEmpty()) {
            System.out.println("История действий пуста.");
            return;
        }

        System.out.println("\n========== ИСТОРИЯ ДЕЙСТВИЙ ==========");
        System.out.printf("%-12s | %-15s | %-20s | %s%n\n",
                "Пользователь", "Действие", "Время", "Детали");
        System.out.println("-".repeat(80));

        for (AuditRecord record : records) {
            String details = record.getDetails() != null ? record.getDetails() : "---";
            System.out.printf("%-12s | %-15s | %-20s | %s%n\n",
                    record.getUsername() != null ? record.getUsername() : "СИСТЕМА",
                    record.getAction(),
                    record.getTimestamp(),
                    truncate(details, 30));
        }
    }

    // ==================== ВЫВОД ДАННЫХ ====================

    /**
     * Печать таблицы товаров.
     */
    private void printProductTable(List<Product> products) {
        if (products.isEmpty()) return;

        System.out.printf("%-4s | %-20s | %-15s | %-12s | %-8s%n + \n",
                "ID", "Название", "Категория", "Бренд", "Цена");
        System.out.println("-".repeat(75));

        for (Product p : products) {
            System.out.printf("%-4d | %-20s | %-15s | %-12s | %.2f%n + \n",
                    p.getId(),
                    truncate(p.getName(), 20),
                    truncate(p.getCategory(), 15),
                    truncate(p.getBrand(), 12),
                    p.getPrice());
        }
    }

    /**
     * Печать деталей товара.
     */
    private void printProductDetails(Product p) {
        System.out.println("\n========== ДЕТАЛИ ТОВАРА ==========");
        System.out.println("ID: " + p.getId());
        System.out.println("Название: " + p.getName());
        System.out.println("Категория: " + p.getCategory());
        System.out.println("Бренд: " + p.getBrand());
        System.out.println("Цена: " + String.format("%.2f", p.getPrice()));
        System.out.println("Описание: " + (p.getDescription() != null ? p.getDescription() : "---"));
        System.out.println("Создан: " + p.getCreatedDate());
        System.out.println("Изменен: " + p.getModifiedDate());
    }

    /**
     * Печать метрик кэша.
     */
    private void printCacheMetrics() {
        System.out.println("\n========== МЕТРИКИ КЭША ==========");
        System.out.println("Размер кэша: " + cacheService.size() + "/" + cacheService.getMaxSize());
        System.out.println("Попадания: " + cacheService.getHits());
        System.out.println("Промахи: " + cacheService.getMisses());
        System.out.printf("Hit Ratio: %.2f%%%n\n", cacheService.getHitRatio() * 100);
        System.out.printf("Среднее время запроса: %.3f ms\n%n", cacheService.getAverageQueryTimeMs());
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /**
     * Чтение целого числа.
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число!");
            }
        }
    }

    /**
     * Чтение числа с плавающей точкой.
     */
    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите числовое значение!");
            }
        }
    }

    /**
     * Чтение строки.
     */
    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Подтверждение действия.
     */
    private boolean confirmAction() {
        System.out.print("Вы уверены? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes") || response.equals("да");
    }

    /**
     * Обрезание текста до максимальной длины.
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "---";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }

    // ==================== ПЕЧАТЬ ЗАГОЛОВКОВ ====================

    private void printLoginHeader() {
        System.out.println("\n========================================");
        System.out.println("        ДОБРО ПОЖАЛОВАТЬ");
        System.out.println("    Product Catalog Service");
        System.out.println("========================================\n");
    }

    private void printMainMenuHeader() {
        System.out.println("\n========================================");
        System.out.println("  ГЛАВНОЕ МЕНЮ (Пользователь: " + currentUser + ")");
        System.out.println("========================================\n");
    }
}