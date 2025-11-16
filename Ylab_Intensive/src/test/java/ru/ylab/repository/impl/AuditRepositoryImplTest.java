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
import ru.ylab.model.AuditRecord;
import ru.ylab.model.enums.Action;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для AuditRepositoryImpl с использованием Testcontainers
 * 
 * Тестирует создание и поиск записей аудита в БД PostgreSQL
 */
@Testcontainers
@DisplayName("AuditRepositoryImpl Integration Tests")
class AuditRepositoryImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("test_catalog_db")
        .withUsername("test_user")
        .withPassword("test_pass");

    private static AuditRepositoryImpl repository;

    @BeforeAll
    static void setup() {
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());

        LiquibaseConfig.runMigrations();
        repository = new AuditRepositoryImpl();
    }

    @BeforeEach
    void cleanDatabase() {
        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("SET session_replication_role = replica");
            stmt.execute("DELETE FROM audit.audit_records");
            stmt.execute("SET session_replication_role = default");

        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database", e);
        }
    }

    @Test
    @DisplayName("Should create audit record and return generated ID")
    void testCreateAuditRecord() {
        // Given
        String username = "admin";
        Action action = Action.LOGIN;
        String details = "User logged in successfully";

        // When
        AuditRecord record = repository.create(username, action, details);

        // Then
        assertNotNull(record, "Запись аудита не должна быть null");
        assertNotNull(record.getId(), "ID должен быть сгенерирован");
        assertEquals(username, record.getUsername(), "Username должен совпадать");
        assertEquals(action, record.getAction(), "Action должен совпадать");
        assertEquals(details, record.getDetails(), "Details должны совпадать");
        assertNotNull(record.getTimestamp(), "Timestamp должен быть установлен");
    }

    @Test
    @DisplayName("Should find all audit records")
    void testFindAll() {
        // Given
        repository.create("user1", Action.LOGIN, "Login attempt");
        repository.create("user2", Action.ADD_PRODUCT, "Added new product");
        repository.create("user3", Action.LOGOUT, "Logged out");

        // When
        List<AuditRecord> records = repository.findAll();

        // Then
        assertNotNull(records, "Список не должен быть null");
        assertTrue(records.size() >= 3, "Должно быть минимум 3 записи");
        // Проверяем, что последняя запись первая (ORDER BY timestamp DESC)
        assertEquals("user3", records.get(0).getUsername(), "Последняя запись должна быть первой");
    }

    @Test
    @DisplayName("Should find audit records by username")
    void testFindByUsername() {
        // Given
        String username = "john_doe";
        repository.create(username, Action.LOGIN, "User login");
        repository.create(username, Action.ADD_PRODUCT, "Added product");
        repository.create("other_user", Action.DELETE_PRODUCT, "Deleted product");

        // When
        List<AuditRecord> records = repository.findByUsername(username);

        // Then
        assertNotNull(records, "Список не должен быть null");
        assertTrue(records.size() >= 2, "Должно быть минимум 2 записи для пользователя");
        assertTrue(records.stream().allMatch(r -> r.getUsername().equals(username)),
            "Все записи должны быть от нужного пользователя");
    }

    @Test
    @DisplayName("Should return empty list for non-existent username")
    void testFindByUsernameNotFound() {
        // When
        List<AuditRecord> records = repository.findByUsername("non_existent_user_xyz");

        // Then
        assertNotNull(records, "Список не должен быть null");
        assertTrue(records.isEmpty(), "Список должен быть пустой");
    }

    @Test
    @DisplayName("Should find audit records by action")
    void testFindByAction() {
        // Given
        Action action = Action.UPDATE_PRODUCT;
        repository.create("user1", action, "Updated product ID 1");
        repository.create("user2", action, "Updated product ID 2");
        repository.create("user3", Action.DELETE_PRODUCT, "Deleted product ID 1");

        // When
        List<AuditRecord> records = repository.findByAction(action.toString());

        // Then
        assertNotNull(records, "Список не должен быть null");
        assertTrue(records.size() >= 2, "Должно быть минимум 2 записи с action UPDATE_PRODUCT");
        assertTrue(records.stream().allMatch(r -> r.getAction() == action),
            "Все записи должны иметь нужное действие");
    }

    @Test
    @DisplayName("Should find audit records after timestamp")
    void testFindAfterTimestamp() throws InterruptedException {
        // Given
        LocalDateTime before = LocalDateTime.now().minusMinutes(1);
        LocalDateTime recordTime = LocalDateTime.now();
        
        repository.create("user1", Action.LOGIN, "Before time");
        Thread.sleep(100); // Небольшая задержка
        
        LocalDateTime after = LocalDateTime.now();
        
        repository.create("user2", Action.LOGOUT, "After time");

        // When
        List<AuditRecord> records = repository.findAfterTimestamp(after.minusSeconds(5));

        // Then
        assertNotNull(records, "Список не должен быть null");
        assertTrue(records.size() >= 1, "Должна быть минимум 1 запись после времени");
    }

    @Test
    @DisplayName("Should test all action types")
    void testAllActionTypes() {
        // Test all enum values
        Action[] actions = Action.values();
        
        for (Action action : actions) {
            // When
            AuditRecord record = repository.create("test_user", action, "Testing " + action);

            // Then
            assertNotNull(record, "Запись не должна быть null для action: " + action);
            assertEquals(action, record.getAction(), "Action должен совпадать: " + action);
        }
    }

    @Test
    @DisplayName("Should store and retrieve timestamp correctly")
    void testTimestampStorage() {
        // Given
        LocalDateTime before = LocalDateTime.now();
        
        // When
        AuditRecord record = repository.create("timestamp_user", Action.LOGIN, "Testing timestamp");
        
        LocalDateTime after = LocalDateTime.now();

        // Then
        assertNotNull(record.getTimestamp(), "Timestamp должен быть установлен");
        assertTrue(record.getTimestamp().isAfter(before.minusSeconds(1)),
            "Timestamp должен быть позже чем before");
        assertTrue(record.getTimestamp().isBefore(after.plusSeconds(1)),
            "Timestamp должен быть раньше чем after");
    }

    @Test
    @DisplayName("Should handle long details string")
    void testLongDetails() {
        // Given
        String longDetails = "This is a very long details string. " +
            "It contains comprehensive information about the audit event. " +
            "The system should be able to handle lengthy text entries without truncation. " +
            "This is important for capturing detailed audit trails in production environments.";
        
        // When
        AuditRecord record = repository.create("detailed_user", Action.ADD_PRODUCT, longDetails);

        // Then
        assertNotNull(record, "Запись не должна быть null");
        assertEquals(longDetails, record.getDetails(), "Long details должны совпадать");
    }

    @Test
    @DisplayName("Should handle special characters in username and details")
    void testSpecialCharacters() {
        // Given
        String username = "user@domain.com";
        String details = "Action with special chars: !@#$%^&*()_+-=[]{}|;:',.<>?/`~";
        
        // When
        AuditRecord record = repository.create(username, Action.LOGIN, details);

        // Then
        assertNotNull(record, "Запись не должна быть null");
        assertEquals(username, record.getUsername(), "Username должен совпадать");
        assertEquals(details, record.getDetails(), "Details должны совпадать");
    }

    @Test
    @DisplayName("Should delete audit records older than timestamp")
    void testDeleteOlderThan() throws InterruptedException {
        // Given
        repository.create("old_user", Action.LOGIN, "Old record");
        Thread.sleep(500); // Небольшая задержка
        
        LocalDateTime cutoffTime = LocalDateTime.now();
        Thread.sleep(500); // Ещё задержка
        
        repository.create("new_user", Action.LOGOUT, "New record");

        // When
        repository.deleteOlderThan(cutoffTime);
        boolean isDeleted = repository.findAll().stream().anyMatch(record -> record.getTimestamp().isBefore(cutoffTime));

        // Then
        assertFalse(isDeleted, "Старые записи должны быть удалены");
    }

    @Test
    @DisplayName("Should return chronological order (newest first)")
    void testChronologicalOrder() throws InterruptedException {
        // Given
        repository.create("user1", Action.LOGIN, "First action");
        Thread.sleep(50);
        repository.create("user2", Action.ADD_PRODUCT, "Second action");
        Thread.sleep(50);
        repository.create("user3", Action.UPDATE_PRODUCT, "Third action");

        // When
        List<AuditRecord> records = repository.findAll();

        // Then
        assertNotNull(records, "Список не должен быть null");
        assertTrue(records.size() >= 3, "Должно быть минимум 3 записи");
        
        // Проверяем порядок (самые новые первыми)
        for (int i = 0; i < records.size() - 1; i++) {
            assertTrue(
                records.get(i).getTimestamp().isAfter(records.get(i + 1).getTimestamp())
                    || records.get(i).getTimestamp().equals(records.get(i + 1).getTimestamp()),
                "Записи должны быть в обратном хронологическом порядке"
            );
        }
    }

    @Test
    @DisplayName("Should combine filters (username and action)")
    void testCombineFilters() {
        // Given
        String targetUser = "filter_test_user";
        Action targetAction = Action.DELETE_PRODUCT;
        
        repository.create(targetUser, targetAction, "Target record");
        repository.create(targetUser, Action.ADD_PRODUCT, "Other action");
        repository.create("other_user", targetAction, "Other user");

        // When
        List<AuditRecord> byUser = repository.findByUsername(targetUser);

        // Filter locally
        List<AuditRecord> combined = byUser.stream()
            .filter(r -> r.getAction() == targetAction)
            .toList();

        // Then
        assertTrue(combined.size() >= 1, "Должна быть найдена целевая запись");
        assertTrue(combined.stream().allMatch(
            r -> r.getUsername().equals(targetUser) && r.getAction() == targetAction),
            "Все записи должны совпадать с фильтрами"
        );
    }

    @Test
    @DisplayName("Should count total audit records")
    void testTotalRecordCount() {
        // Given
        int initialCount = repository.findAll().size();
        repository.create("count_user1", Action.LOGIN, "Record 1");
        repository.create("count_user2", Action.LOGOUT, "Record 2");

        // When
        int newCount = repository.findAll().size();

        // Then
        assertEquals(initialCount + 2, newCount, "Должно быть добавлено 2 новых записи");
    }
}
