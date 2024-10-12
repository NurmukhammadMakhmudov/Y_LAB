package com.example.y_lab;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.User;
import com.example.y_lab.services.HabitService;
import com.example.y_lab.services.HabitTrackingService;
import com.example.y_lab.services.UserService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleApp {
    private final UserService userService = new UserService();
    private final HabitService habitService = new HabitService();
    private final HabitTrackingService habitTrackingService = new HabitTrackingService();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> System.exit(0);
            }
        }
    }

    private void register() {
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

    private void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        try {
            User user = userService.login(email, password);
            System.out.println("Login successful. Welcome, " + user.getName());
            userMenu(user);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void userMenu(User user) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Create Habit");
            System.out.println("2. View Habits");
            System.out.println("3. Edit Habit");
            System.out.println("4. Delete Habit");
            System.out.println("5. Mark Habit Completion");
            System.out.println("6. Logout");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            switch (choice) {
                case 1:
                    createHabit(user);
                    break;
                case 2:
                    viewHabits(user);
                    break;
                case 3:
                    editHabit(user);
                    break;
                case 4:
                    deleteHabit(user);
                    break;
                case 5:
                    markCompletion(user);
                    break;
                case 6:
                    return;
            }
        }
    }

    private void createHabit(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Frequency (daily/weekly): ");
        String frequency = scanner.nextLine();
        habitService.addHabit(user, title, description, frequency);
        System.out.println("Habit created.");
    }

    private void viewHabits(User user) {
        user.getHabits().forEach(habit -> System.out.println(habit.getId() + ". " + habit.getTitle()));
    }

    private void editHabit(User user) {
        Scanner scanner = new Scanner(System.in);
        viewHabits(user);
        System.out.print("Enter habit ID to edit: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline
        Optional<Habit> habit = habitService.findHabitById(user, id);
        if (habit.isPresent()) {
            System.out.print("New Title: ");
            String title = scanner.nextLine();
            System.out.print("New Description: ");
            String description = scanner.nextLine();
            System.out.print("New Frequency (daily/weekly): ");
            String frequency = scanner.nextLine();
            habitService.editHabit(habit.get(), title, description, frequency);
            System.out.println("Habit updated.");
        } else {
            System.out.println("Habit not found.");
        }
    }

    private void deleteHabit(User user) {
        Scanner scanner = new Scanner(System.in);
        viewHabits(user);
        System.out.print("Enter habit ID to delete: ");
        int id = scanner.nextInt();
        Optional<Habit> habit = habitService.findHabitById(user, id);
        if (habit.isPresent()) {
            habitService.deleteHabit(user, habit.get());
            System.out.println("Habit deleted.");
        } else {
            System.out.println("Habit not found.");
        }
    }

    private void markCompletion(User user) {
        Scanner scanner = new Scanner(System.in);
        viewHabits(user);
        System.out.print("Enter habit ID to mark completion: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline
        Optional<Habit> habit = habitService.findHabitById(user, id);
        if (habit.isPresent()) {
            System.out.print("Completion date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());
            System.out.print("Completed? (true/false): ");
            boolean completed = scanner.nextBoolean();
            habitTrackingService.markHabitCompletion(habit.get(), date, completed);
            System.out.println("Habit completion marked.");
        } else {
            System.out.println("Habit not found.");
        }
    }

    public static void main(String[] args) {
        new ConsoleApp().start();
    }
}