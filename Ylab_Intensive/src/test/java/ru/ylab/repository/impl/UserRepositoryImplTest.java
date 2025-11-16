package ru.ylab.repository.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ylab.config.DatabaseConfig;
import ru.ylab.config.LiquibaseConfig;
import ru.ylab.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для UserRepositoryImpl с использованием Testcontainers
 * 
 * Тестирует CRUD операции и поиск пользователей в реальной БД PostgreSQL
 */
@Testcontainers
@DisplayName("UserRepositoryImpl Integration Tests")
class UserRepositoryImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test_catalog_db")
        .withUsername("test_user")
        .withPassword("test_pass");

    private static UserRepositoryImpl repository;

    @BeforeAll
    static void setup() {
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());
        LiquibaseConfig.runMigrations();
        repository = new UserRepositoryImpl();
    }

    @BeforeEach
    void cleanDatabase() {
        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("SET session_replication_role = replica");
            stmt.execute("DELETE FROM catalog.users");
            stmt.execute("DELETE FROM catalog.products");
            stmt.execute("DELETE FROM audit.audit_records");
            stmt.execute("SET session_replication_role = default");

        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database", e);
        }
    }

    @Test
    @DisplayName("Should create user and return generated ID")
    void testCreateUser() {
        // Given
        String username = "john1";
        String password = "secure_password";

        // When
        repository.findAll().forEach(System.out::println);
        User user = repository.create(username, password);

        // Then
        assertNotNull(user, "Пользователь не должен быть null");
        assertNotNull(user.getId(), "ID пользователя должен быть сгенерирован");
        assertEquals(username, user.getUsername(), "Username должен совпадать");
        assertEquals(password, user.getPassword(), "Password должен совпадать");
        assertNotNull(user.getCreatedAt(), "createdAt должен быть установлен");
    }

    @Test
    @DisplayName("Should find user by username")
    void testFindByUsername() {
        // Given
        String username = "jane_smith";
        String password = "password123";
        repository.create(username, password);

        // When
        Optional<User> found = repository.findByUsername(username);

        // Then
        assertTrue(found.isPresent(), "Пользователь должен быть найден");
        assertEquals(username, found.get().getUsername(), "Username должен совпадать");
        assertEquals(password, found.get().getPassword(), "Password должен совпадать");
    }

    @Test
    @DisplayName("Should return empty Optional for non-existent user")
    void testFindByUsernameNotFound() {
        // When
        Optional<User> found = repository.findByUsername("non_existent_user_12345");

        // Then
        assertFalse(found.isPresent(), "Optional должен быть пустой для несуществующего пользователя");
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindById() {
        // Given
        User created = repository.create("find_me", "password");
        long userId = created.getId();

        // When
        Optional<User> found = repository.findById(userId);

        // Then
        assertTrue(found.isPresent(), "Пользователь должен быть найден по ID");
        assertEquals(userId, found.get().getId(), "ID должен совпадать");
        assertEquals("find_me", found.get().getUsername(), "Username должен совпадать");
    }

    @Test
    @DisplayName("Should return empty Optional for non-existent ID")
    void testFindByIdNotFound() {
        // When
        Optional<User> found = repository.findById(999999L);

        // Then
        assertFalse(found.isPresent(), "Optional должен быть пустой для несуществующего ID");
    }

    @Test
    @DisplayName("Should update user password")
    void testUpdatePassword() {
        // Given
        User user = repository.create("update_test", "old_password");
        long userId = user.getId();
        String newPassword = "new_secure_password";

        // When
        boolean updated = repository.update(userId, newPassword);

        // Then
        assertTrue(updated, "Обновление должно быть успешным");
        
        Optional<User> found = repository.findById(userId);
        assertTrue(found.isPresent(), "Пользователь должен быть найден");
        assertEquals(newPassword, found.get().getPassword(), "Password должен быть обновлен");
    }

    @Test
    @DisplayName("Should return false when updating non-existent user")
    void testUpdateNonExistentUser() {
        // When
        boolean updated = repository.update(999999L, "new_password");

        // Then
        assertFalse(updated, "Обновление несуществующего пользователя должно вернуть false");
    }

    @Test
    @DisplayName("Should delete user")
    void testDeleteUser() {
        // Given
        User user = repository.create("delete_me", "password");
        long userId = user.getId();

        // When
        boolean deleted = repository.delete(userId);

        // Then
        assertTrue(deleted, "Удаление должно быть успешным");
        
        Optional<User> found = repository.findById(userId);
        assertFalse(found.isPresent(), "Пользователь не должен быть найден после удаления");
    }

    @Test
    @DisplayName("Should return false when deleting non-existent user")
    void testDeleteNonExistentUser() {
        // When
        boolean deleted = repository.delete(999999L);

        // Then
        assertFalse(deleted, "Удаление несуществующего пользователя должно вернуть false");
    }

    @Test
    @DisplayName("Should check if username exists")
    void testUserExists() {
        // Given
        String username = "exists_test";
        repository.create(username, "password");

        // When
        boolean exists = repository.exists(username);

        // Then
        assertTrue(exists, "exists() должен вернуть true для существующего пользователя");
    }

    @Test
    @DisplayName("Should return false if username does not exist")
    void testUserNotExists() {
        // When
        boolean exists = repository.exists("definitely_non_existent_user_xyz");

        // Then
        assertFalse(exists, "exists() должен вернуть false для несуществующего пользователя");
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        // Given - очистим и создадим тестовых пользователей
        repository.create("user1", "pass1");
        repository.create("user2", "pass2");
        repository.create("user3", "pass3");

        // When
        var users = repository.findAll();

        // Then
        assertNotNull(users, "Список пользователей не должен быть null");
        assertTrue(users.size() >= 3, "Должно быть минимум 3 пользователя");
    }

    @Test
    @DisplayName("Should prevent duplicate usernames")
    void testPreventDuplicateUsername() {
        // Given
        String username = "unique_user";
        repository.create(username, "password1");

        // When & Then
        // Попытка создать пользователя с тем же username должна привести к ошибке или null
        assertThrows(RuntimeException.class, 
            () -> repository.create(username, "password2"),
            "Создание пользователя с дублирующимся username должно выбросить исключение");
    }

    @Test
    @DisplayName("Should store and retrieve created_at timestamp")
    void testCreatedAtTimestamp() {
        // When
        User user = repository.create("timestamp_test", "password");

        // Then
        assertNotNull(user.getCreatedAt(), "createdAt должен быть установлен");
        assertTrue(user.getCreatedAt().isBefore(java.time.LocalDateTime.now().plusSeconds(1)),
            "createdAt должен быть близок к текущему времени");
    }

    @Test
    @DisplayName("Should handle special characters in password")
    void testSpecialCharactersInPassword() {
        // Given
        String username = "special_chars";
        String password = "P@ssw0rd!#$%^&*()_+-=[]{}|;:',.<>?/`~";

        // When
        User user = repository.create(username, password);
        Optional<User> found = repository.findByUsername(username);

        // Then
        assertTrue(found.isPresent(), "Пользователь должен быть найден");
        assertEquals(password, found.get().getPassword(), "Пароль со спецсимволами должен сохраниться");
    }
}
