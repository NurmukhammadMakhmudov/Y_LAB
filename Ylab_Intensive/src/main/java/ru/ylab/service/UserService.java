package ru.ylab.service;

import ru.ylab.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями маркетплейса.
 * <p>
 * Предоставляет функции регистрации новых пользователей и аутентификации (проверки пароля).
 * Хранит информацию о пользователях (логин и хэш пароля) в памяти.
 * </p>
 * <p>
 * <strong>Безопасность:</strong>
 * <ul>
 *   <li>Пароли никогда не хранятся в открытом виде, только их хэши</li>
 *   <li>При аутентификации сравниваются хэши, а не сами пароли</li>
 *   <li>Минимальная длина пароля — 4 символа</li>
 * </ul>
 * </p>
 * <p>
 * <strong>Потокобезопасность:</strong> сервис НЕ является потокобезопасным.
 * </p>
 * <p>
 * Пример использования:
 * <pre>{@code
 * UserService userService = new UserServiceImpl(appData);
 *
 * // Регистрация нового пользователя
 * userService.register("admin", "admin123");
 *
 * // Проверка пароля при входе
 * if (userService.authenticate("admin", "admin123")) {
 *     System.out.println("Вход успешен");
 * } else {
 *     System.out.println("Неверный пароль");
 * }
 * }</pre>
 * </p>
 *
 * @author Makhmudov Nurmukhammad
 * @version 1.0
 * @since 2025-11-09
 */
public interface UserService {

    /**
     * Регистрирует нового пользователя с указанными учётными данными.
     * <p>
     * <strong>Валидация:</strong>
     * <ul>
     *   <li>Логин не может быть {@code null} или пустым</li>
     *   <li>Пароль должен содержать минимум 4 символа</li>
     *   <li>Пользователь с таким логином не должен уже существовать</li>
     * </ul>
     * </p>
     *
     * @param username логин пользователя (не может быть {@code null} или пустым)
     * @param password пароль в открытом виде (должен содержать минимум 4 символа)
     * @throws IllegalArgumentException если логин пустой или {@code null}
     * @throws IllegalArgumentException если пароль меньше 4 символов
     * @throws IllegalArgumentException если пользователь с таким логином уже зарегистрирован
     * @see #login(String, String)
     */
    boolean register(String username, String password);

    /**
     * Проверяет учётные данные пользователя.
     * <p>
     * Сравнивает хэш переданного пароля с сохранённым хэшем.
     * </p>
     *
     * @param username логин пользователя (не может быть {@code null})
     * @param password пароль в открытом виде (не может быть {@code null})
     * @return {@code true} если пользователь существует и пароль верен; {@code false} иначе
     * @throws NullPointerException если username или password равны {@code null}
     * @see #register(String, String)
     */

    Optional<User> login(String username, String password);

    boolean changePassword(String username, String oldPassword, String newPassword);

    boolean userExists(String username);

    List<User> getAllUsers();

    void logout(String username);
}