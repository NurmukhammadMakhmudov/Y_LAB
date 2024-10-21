package com.example.y_lab.services;

import com.example.y_lab.models.User;
import com.example.y_lab.repositories.HabitRepository;
import com.example.y_lab.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;



    public UserService(UserRepository userRepository, HabitRepository habitRepository) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
    }

    public void registerUser(String email, String password, String name) {
        Optional<User> user1 = userRepository.findByEmail(email);
        if (user1.isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setName(name);
            userRepository.save(user);
        }
        else
            throw new IllegalArgumentException("Email is already taken.");
    }

    public User login(String email, String password) {
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);
        if (user.isPresent() )
            if (!user.get().isBlocked())
                return user.get();
            else
                throw new IllegalArgumentException("User Blocked");
        throw new IllegalArgumentException("Invalid email or password.");
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public void editUserProfile(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        updateUser(user, newName, newEmail, newPassword);
        System.out.println("Profile updated successfully.");
    }

    public void updateUser(User user, String name, String email, String password)
    {
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        userRepository.save(user);

    }


    public void deleteUser(User user) {
        System.out.println("Are you sure you want to delete your account? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            userRepository.delete(user);
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
            habitRepository.findByUser(user).forEach(habit -> {
                System.out.println("  Habit: " + habit.getTitle() + " - " + habit.getFrequency());
            });
        });
    }

    public void blockUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user email to block: ");
        String email = scanner.nextLine();
        if (userRepository.findByEmail(email).isPresent()) {
            User user = userRepository.findByEmail(email).get();
            user.setBlocked(true);
            userRepository.save(user);
            System.out.println("User " + email + " has been blocked.");
        } else {
            System.out.println("User not found.");
        }
    }

    public void deleteUserAsAdmin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user email to delete: ");
        String email = scanner.nextLine();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent())
        {
            userRepository.delete(user.get());
            System.out.println("User " + email + " has been deleted.");
        }
        else
            System.out.println("User" + email + " not found");

    }
}