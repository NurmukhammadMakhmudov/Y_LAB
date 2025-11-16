package main.java.ru.ylab.service.impl;


import main.java.ru.ylab.model.AppData;
import main.java.ru.ylab.model.User;
import main.java.ru.ylab.service.UserService;

import java.util.Map;

public class UserServiceImpl implements UserService {

    private final Map<String, User> users;

    public UserServiceImpl(AppData appData) {
        this.users = appData.getUsers();
    }

    public void register(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 4 символа");
        }
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        User user = new User(username, hashPassword(password));
        users.put(username, user);
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }
        return user.getPasswordHash().equals(hashPassword(password));
    }

    private String hashPassword(String password) {
        return "hash_" + password;
    }
}
