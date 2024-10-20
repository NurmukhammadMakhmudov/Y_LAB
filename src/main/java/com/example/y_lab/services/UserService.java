package com.example.y_lab.services;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.User;
import com.example.y_lab.repositories.UserRepository;

import java.util.*;

public class UserService {

    private final UserRepository userRepository = new UserRepository();

    private long userIdCounter = 1;

    public void registerUser(String email, String password, String name) {
        if (userRepository.isEmailTaken(email)) {
            throw new IllegalArgumentException("Email is already taken.");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setName(name);
        userRepository.createUser(user);
    }

    public User login(String email, String password) {
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);
        if (user.isPresent())
            return user.get();
        throw new IllegalArgumentException("Invalid email or password.");
    }


    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }


    public void editUserProfile(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        userRepository.updateUser(user, newName, newEmail, newPassword);
        System.out.println("Profile updated successfully.");
    }

    public void deleteUser(User user) {
        System.out.println("Are you sure you want to delete your account? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            userRepository.deleteUser(user);
            System.out.println("Account deleted.");
            // Автоматический выход после удаления аккаунта
            System.exit(0);
        } else {
            System.out.println("Account deletion canceled.");
        }
    }

    public void viewAllUsers() {
        List<User> users = getAllUsers();
        users.forEach(user -> {
            System.out.println("User: " + user.getEmail() + " - " + user.getName());
            user.getHabits().forEach(habit -> {
                System.out.println("  Habit: " + habit.getTitle() + " - " + habit.getFrequency());
            });
        });
    }

    public void blockUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user email to block: ");
        String email = scanner.nextLine();
        User user = userRepository.findUserByEmail(email);
        if (user != null) {
            userRepository.blockUser(user);
            System.out.println("User " + email + " has been blocked.");
        } else {
            System.out.println("User not found.");
        }
    }

    public void deleteUserAsAdmin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user email to delete: ");
        String email = scanner.nextLine();
        userRepository.deleteUserAsAdmin(email);
        System.out.println("User " + email + " has been deleted.");
    }
}