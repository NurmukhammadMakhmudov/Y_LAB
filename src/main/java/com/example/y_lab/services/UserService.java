package com.example.y_lab.services;

import com.example.y_lab.models.User;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private Map<Integer, User> userRepository = new HashMap<>();
    private int userIdCounter = 1;

    public void registerUser(String email, String password, String name) {
        if (isEmailTaken(email)) {
            throw new IllegalArgumentException("Email is already taken.");
        }
        User user = new User(userIdCounter++, email, password, name);
        userRepository.put(user.getId(), user);
    }

    public User login(String email, String password) {
        for (User user : userRepository.values()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new IllegalArgumentException("Invalid email or password.");
    }

    public void updateUser(User user, String name, String email, String password) {
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
    }

    public void deleteUser(User user) {
        userRepository.remove(user.getId());
    }

    private boolean isEmailTaken(String email) {
        return userRepository.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}