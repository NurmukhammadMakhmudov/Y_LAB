import main.java.ru.ylab.model.AppData;
import main.java.ru.ylab.model.Product;
import main.java.ru.ylab.repository.ProductRepository;
import main.java.ru.ylab.service.AuditService;
import main.java.ru.ylab.service.CacheService;
import main.java.ru.ylab.service.CatalogService;
import main.java.ru.ylab.service.UserService;
import main.java.ru.ylab.service.impl.*;
import main.java.ru.ylab.ui.ConsoleMenu;


import java.util.Scanner;

public class Main {

    private static AppData appData;
    private static DataStorageImpl dataStorage;

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  Маркетплейс: Product Catalog Service");
        System.out.println("==============================================\n");

        dataStorage = new DataStorageImpl();

        appData = dataStorage.load();


        if (appData.getProducts().isEmpty()) {
            System.out.println("Первый запуск - создаём тестовые данные...\n");
            initializeTestData();
        }

        // Инициализация всех компонентов
        ProductRepository productRepository = new ProductRepository(appData);
        AuditService auditService = new AuditServiceImpl(appData);
        UserService userService = new UserServiceImpl(appData);
        CacheService cacheService = new CacheServiceImpl();
        CatalogService catalogService = new CatalogServiceImpl(productRepository, auditService, cacheService);



        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\nСохранение данных перед выходом...");
            dataStorage.save(appData);
            System.out.println("Данные сохранены. До свидания!");
        }));

        // Запуск консольного меню
        Scanner scanner = new Scanner(System.in);
        ConsoleMenu menu = new ConsoleMenu(catalogService, userService, auditService, scanner, cacheService);

        try {
            menu.start();
        } finally {
            scanner.close();
        }

        System.out.println("\nСпасибо за использование системы! До свидания!");
    }

    private static void initializeTestData() {

        UserServiceImpl userService = new UserServiceImpl(appData);

        // Создаём тестовых пользователей
        userService.register("admin", "admin123");
        userService.register("user1", "password");

        // Создаём тестовые товары
        ProductRepository repository = new ProductRepository(appData);
        repository.add(new Product(0, "MacBook Pro 16", "Electronics", "Apple", 2499.99, "Мощный ноутбук для профессионалов"));
        repository.add(new Product(0, "iPhone 15 Pro", "Electronics", "Apple", 999.99, "Флагманский смартфон"));
        repository.add(new Product(0, "Dell XPS 15", "Electronics", "Dell", 1799.99, "Ультрабук для работы"));
        repository.add(new Product(0, "Samsung Galaxy S24", "Electronics", "Samsung", 899.99, "Топовый Android-смартфон"));
        repository.add(new Product(0, "Nike Air Max", "Shoes", "Nike", 129.99, "Спортивные кроссовки"));
        repository.add(new Product(0, "Adidas Ultraboost", "Shoes", "Adidas", 179.99, "Беговые кроссовки"));
        repository.add(new Product(0, "Sony WH-1000XM5", "Electronics", "Sony", 399.99, "Наушники с шумоподавлением"));
        repository.add(new Product(0, "Levi's 501 Jeans", "Clothing", "Levi's", 89.99, "Классические джинсы"));
        repository.add(new Product(0, "The North Face Jacket", "Clothing", "The North Face", 249.99, "Зимняя куртка"));
        repository.add(new Product(0, "Kindle Paperwhite", "Electronics", "Amazon", 139.99, "Электронная книга"));

        System.out.println("Создано товаров: " + appData.getProducts().size());
        System.out.println("Создано пользователей: " + appData.getUsers().size());
        System.out.println("Логин: admin / admin123 или user1 / password\n");

        // Сохраняем начальные данные
        dataStorage.save(appData);
    }
}
