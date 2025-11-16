package ru.ylab.repository;

import ru.ylab.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с пользователями
 *
 * Определяет все операции для работы с учётными записями пользователей:
 * - Создание новых пользователей (регистрация)
 * - Поиск пользователей по имени и ID
 * - Обновление информации (пароль)
 * - Удаление пользователей
 * - Проверка существования пользователя
 * - Получение статистики
 *
 * Все операции выполняются через PostgreSQL с помощью JDBC.
 * ID пользователей генерируются автоматически с помощью SEQUENCE.
 * Username должен быть уникальным (UNIQUE constraint в БД).
 *
 * @author Y.Lab
 * @author Makhmudov Nurmukhammad
 * @version 2.0
 */
public interface UserRepository {

    /**
     * Создать нового пользователя
     * ID генерируется автоматически базой данных через SEQUENCE
     * Username должен быть уникальным в системе
     *
     * @param username Уникальное имя пользователя
     * @param password Пароль пользователя (в текущей версии хранится в открытом виде)
     * @return Созданный пользователь с назначенным ID
     */
    User create(String username, String password);

    /**
     * Найти пользователя по имени пользователя
     * Используется при входе в систему для проверки пароля
     *
     * @param username Имя пользователя
     * @return Optional с пользователем если найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Найти пользователя по ID
     *
     * @param id Идентификатор пользователя
     * @return Optional с пользователем если найден
     */
    Optional<User> findById(long id);

    /**
     * Получить всех пользователей системы
     *
     * @return Список всех пользователей
     */
    List<User> findAll();

    /**
     * Обновить пароль пользователя
     *
     * @param id ID пользователя
     * @param newPassword Новый пароль
     * @return true если обновление успешно, false если пользователь не найден
     */
    boolean update(long id, String newPassword);

    /**
     * Удалить пользователя из системы
     *
     * @param id ID пользователя для удаления
     * @return true если удаление успешно, false если пользователь не найден
     */
    boolean delete(long id);

    /**
     * Проверить существование пользователя по имени
     * Используется для проверки уникальности при регистрации
     *
     * @param username Имя пользователя
     * @return true если пользователь существует, false если не найден
     */
    boolean exists(String username);

    /**
     * Получить общее количество пользователей в системе
     *
     * @return Количество зарегистрированных пользователей
     */
    int count();
}