package com.example.y_lab.services;

import com.example.y_lab.ConsoleApp;
import com.example.y_lab.models.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Scanner;

@Service
public class AuthenticationService {

    private final UserService userService;

    public AuthenticationService(UserService userService) {
        this.userService = userService;

    }



    public void register() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        try {
            userService.registerUser(email, password, name);
            System.out.println("Registration successful.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public Optional<User> login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        try {
            User user = userService.login(email, password);
            System.out.println("Login successful. Welcome, " + user.getName());
            return Optional.of(user);
        } catch (Exception e) {
            System.out.println("Error: User not found");
        }
        return Optional.empty();
    }
}
