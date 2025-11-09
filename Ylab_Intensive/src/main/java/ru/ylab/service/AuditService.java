package main.java.ru.ylab.service;

import main.java.ru.ylab.model.AuditRecord;
import main.java.ru.ylab.model.enums.Action;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для логирования и отслеживания действий пользователей в системе.
 * <p>
 * Записывает все значимые операции (добавление, удаление, обновление товаров,
 * вход/выход пользователей) в аудит-лог для последующего анализа и отладки.
 * </p>
 * <p>
 * Каждая запись содержит:
 * <ul>
 *   <li>Имя пользователя, выполнившего действие</li>
 *   <li>Тип действия (ADD, DELETE, UPDATE, SEARCH, LOGIN, LOGOUT)</li>
 *   <li>Дополнительные детали (например, ID товара, статус операции)</li>
 *   <li>Временную метку операции</li>
 * </ul>
 * </p>
 * <p>
 * Пример использования:
 * <pre>{@code
 * AuditService auditService = new AuditServiceImpl(appData);
 * auditService.log("admin", Action.ADD, "Added product: MacBook Pro (ID: 1)");
 * auditService.log("user1", Action.DELETE, "Deleted product (ID: 5)");
 *
 * List<AuditRecord> adminActions = auditService.getRecordsByUser("admin");
 * List<AuditRecord> recentOperations = auditService.getRecordsAfter(LocalDateTime.now().minusHours(1));
 * }</pre>
 * </p>
 *
 * @see AuditRecord
 * @see Action
 * @author Ваше Имя
 * @version 1.0
 * @since 2025-11-09
 */
public interface AuditService {

    /**
     * Записывает действие в аудит-лог.
     * <p>
     * Запись автоматически получает текущее время выполнения операции.
     * </p>
     *
     * @param username имя пользователя, выполнившего действие (может быть {@code null} для системных операций)
     * @param action тип действия (не может быть {@code null})
     * @param details подробное описание действия, например "Added product: Laptop (ID: 1)" (может быть {@code null})
     * @see Action
     */
    void log(String username, Action action, String details);

    /**
     * Возвращает все записи аудита.
     * <p>
     * Результаты не кэшируются, всегда возвращается полный список.
     * </p>
     *
     * @return список всех аудит-записей (может быть пустым, но никогда {@code null})
     * @see #getRecordsByUser(String)
     */
    List<AuditRecord> getAllRecords();

    /**
     * Возвращает все действия конкретного пользователя.
     * <p>
     * Используется для отслеживания активности пользователя и проверки истории его операций.
     * </p>
     *
     * @param username имя пользователя (не может быть {@code null} или пустым)
     * @return список действий пользователя (может быть пустым, но никогда {@code null})
     * @throws IllegalArgumentException если username {@code null} или пустой
     * @see #getAllRecords()
     */
    List<AuditRecord> getRecordsByUser(String username);

    /**
     * Возвращает все записи определённого типа действия.
     * <p>
     * Используется для анализа конкретных операций (например, все добавления товаров).
     * </p>
     *
     * @param action название действия (строка, должна совпадать с {@link Action#getActionName()})
     * @return список записей с указанным типом действия (может быть пустым, но никогда {@code null})
     * @see Action
     * @see #getAllRecords()
     */
    List<AuditRecord> getRecordsByAction(String action);

    /**
     * Возвращает записи, создённые после указанного времени.
     * <p>
     * Используется для получения недавних действий, например операций за последний час.
     * </p>
     *
     * @param dateTime точка времени, после которой нужно получить записи (не может быть {@code null})
     * @return список записей, создённых после указанного времени (может быть пустым, но никогда {@code null})
     * @throws IllegalArgumentException если dateTime {@code null}
     */
    List<AuditRecord> getRecordsAfter(LocalDateTime dateTime);

    /**
     * Возвращает общее количество аудит-записей.
     *
     * @return количество записей ({@code >= 0})
     */
    int getRecordCount();
}
