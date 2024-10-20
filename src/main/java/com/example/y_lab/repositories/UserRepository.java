package com.example.y_lab.repositories;

import com.example.y_lab.models.User;

import java.util.*;

public class UserRepository {
    private Map<Long, User> userRepository = new HashMap<>();
    private int userIdCounter = 1;

    public void createUser(User user) {
        userRepository.put(user.getId(), user);
    }

    public Optional<User> findByEmailAndPassword(String email, String password) {
        for (User user : userRepository.values()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public void deleteUser(User user) {
        userRepository.remove(user.getId());
    }

    public void deleteUserByEmail(String email) throws IllegalAccessException {
        for (User user : userRepository.values()) {
            if (user.getEmail().equals(email)) {
                userRepository.remove(user);
                return;
            }
        }
        throw new IllegalAccessException("No such Email or User");
    }

    public boolean isEmailTaken(String email) {
        return userRepository.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.values());
    }

    public void blockUser(User user) {
        user.setBlocked(true);
    }

    public void deleteUserAsAdmin(String email) {
        try {
            deleteUserByEmail(email);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUser(User user, String name, String email, String password) {
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
    }


    public User findUserByEmail(String email) {
        return userRepository.get(email);
    }
}
