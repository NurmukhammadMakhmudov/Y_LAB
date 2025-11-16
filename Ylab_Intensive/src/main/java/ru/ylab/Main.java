package ru.ylab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ylab.config.LiquibaseConfig;
import ru.ylab.repository.AuditRepository;
import ru.ylab.repository.ProductRepository;
import ru.ylab.repository.UserRepository;
import ru.ylab.repository.impl.AuditRepositoryImpl;
import ru.ylab.repository.impl.ProductRepositoryImpl;
import ru.ylab.repository.impl.UserRepositoryImpl;
import ru.ylab.service.AuditService;
import ru.ylab.service.CacheService;
import ru.ylab.service.CatalogService;
import ru.ylab.service.UserService;
import ru.ylab.service.impl.AuditServiceImpl;
import ru.ylab.service.impl.CacheServiceImpl;
import ru.ylab.service.impl.CatalogServiceImpl;
import ru.ylab.service.impl.UserServiceImpl;
import ru.ylab.ui.ConsoleMenu;

import java.util.Scanner;

/**
 * Главный класс приложения Product Catalog Service
 * <p>
 * Инициализирует все компоненты системы:
 * - База данных PostgreSQL
 * - Liquibase миграции
 * - Repository слой
 * - Service слой
 * - Консольный интерфейс
 *
 * @author Makhmudov Nurmukhammad
 * @version 2.0
 * @since 2025-11-16
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final ProductRepository productRepository = new ProductRepositoryImpl();
    private static final UserRepository userRepository = new UserRepositoryImpl();
    private static final AuditRepository auditRepository = new AuditRepositoryImpl();
    private static final AuditService auditService = new AuditServiceImpl(auditRepository);
    private static final UserService userService = new UserServiceImpl(userRepository, auditService);
    private static final CacheService cacheService = new CacheServiceImpl();
    private static final CatalogService catalogService = new CatalogServiceImpl(productRepository, auditService, cacheService);
    private static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {

        try {
            logger.info("Запуск инициализации базы данных...");
            LiquibaseConfig.runMigrations();
            logger.info("Liquibase миграции выполнены успешно");


            logger.info("Запуск консольного интерфейса...");
            try (scanner) {
                ConsoleMenu menu = new ConsoleMenu(catalogService, userService, auditService, scanner, cacheService);
                menu.start();
            } finally {
                scanner.close();
            }

            System.out.println("\nСпасибо за использование Product Catalog Service!");
        } catch (Exception e) {
            logger.error("Критическая ошибка при запуске приложения", e);
            System.err.println(e.getMessage());
        }
    }

}