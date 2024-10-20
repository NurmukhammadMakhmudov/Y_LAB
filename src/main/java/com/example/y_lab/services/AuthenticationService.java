package com.example.y_lab.services;

import com.example.y_lab.ConsoleApp;
import com.example.y_lab.models.User;

import java.util.Scanner;

public class AuthenticationService {

    private final UserService userService;
    private final ConsoleApp consoleApp;

    public AuthenticationService(UserService userService, ConsoleApp consoleApp) {
        this.userService = userService;
        this.consoleApp = consoleApp;
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

    public void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        try {
            User user = userService.login(email, password);
            System.out.println("Login successful. Welcome, " + user.getName());
            consoleApp.userMenu(user);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
