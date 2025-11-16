package ru.ylab.service.impl;

import ru.ylab.model.User;
import ru.ylab.model.enums.Action;
import ru.ylab.repository.UserRepository;
import ru.ylab.service.AuditService;
import ru.ylab.service.UserService;

import java.util.List;
import java.util.Optional;


public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuditService auditService;

    public UserServiceImpl(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Override
    public boolean register(String username, String password) {
        validateUser(username, password);

        if (userRepository.exists(username)) {
            return false;
        }

        try {
            User user = userRepository.create(username, password);
            auditService.log(username, Action.REGISTER, "User registered: " + user.getUsername());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<User> login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() && user.get().getPassword().equals(password)) {
            auditService.log(username, Action.LOGIN, "User logged in: " + username);
            return user;
        }

        auditService.log(username, Action.LOGIN, "Failed login attempt: " + username);
        return Optional.empty();
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent() && user.get().getPassword().equals(oldPassword)) {
            boolean updated = userRepository.update(user.get().getId(), newPassword);
            if (updated) {
                auditService.log(username, Action.UPDATE_PRODUCT, "Password changed");
            }
            return updated;
        }

        return false;
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.exists(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void logout(String username) {
        auditService.log(username, Action.LOGOUT, "User logged out: " + username);
    }

    private void validateUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
    }
}