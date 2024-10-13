package com.example.y_lab;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.HabitCompletion;
import com.example.y_lab.models.User;
import com.example.y_lab.services.HabitService;
import com.example.y_lab.services.HabitTrackingService;
import com.example.y_lab.services.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
            scanner.nextLine();
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
            System.out.println("6. View Habit Details");
            System.out.println("7. View Habit Statistics");
            System.out.println("8. View Streak for Habit");
            System.out.println("9. Edit Profile");
            System.out.println("10. Delete Account");
            System.out.println("11. View All Users and Habits (Admin)");
            System.out.println("12. Block User (Admin)");
            System.out.println("13. Delete User (Admin)");
            System.out.println("14. Logout");
            int choice = scanner.nextInt();
            scanner.nextLine();
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
                    viewHabitDetails(user);
                    break;
                case 7:
                    viewHabitStatisticsByPeriod(user);
                    break;
                case 8:
                    viewStreakForHabit(user);
                    break;
                case 9:
                    editUserProfile(user);
                    break;
                case 10:
                    deleteUser(user);
                    break;
                case 11:
                    viewAllUsers();
                    break;
                case 12:
                    blockUser();
                    break;
                case 13:
                    deleteUserAsAdmin();
                    break;
                case 14:
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Filter by frequency (daily/weekly) or press Enter to see all: ");
        String filter = scanner.nextLine().toLowerCase();

        user.getHabits().stream()
                .filter(habit -> filter.isEmpty() || habit.getFrequency().toLowerCase().equals(filter))
                .forEach(habit -> System.out.println(habit.getId() + ". " + habit.getTitle() + " (" + habit.getFrequency() + ")"));
    }
    private void viewHabitDetails(User user) {
        Scanner scanner = new Scanner(System.in);
        viewHabits(user); // Сначала покажем список всех привычек
        System.out.print("Enter habit ID to view details: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Optional<Habit> habit = habitService.findHabitById(user, id);
        if (habit.isPresent()) {
            Habit h = habit.get();
            System.out.println("Title: " + h.getTitle());
            System.out.println("Description: " + h.getDescription());
            System.out.println("Frequency: " + h.getFrequency());
            System.out.println("Completions:");
            h.getCompletions().forEach(c -> System.out.println("Date: " + c.getDate() + ", Completed: " + c.isCompleted()));
        } else {
            System.out.println("Habit not found.");
        }
    }

    private void editHabit(User user) {
        Scanner scanner = new Scanner(System.in);
        viewHabits(user);
        System.out.print("Enter habit ID to edit: ");
        int id = scanner.nextInt();
        scanner.nextLine();
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

            if (date.isAfter(LocalDate.now())) {
                System.out.println("Error: Completion date cannot be in the future.");
                return;
            }

            System.out.print("Completed? (true/false): ");
            boolean completed = scanner.nextBoolean();
            habitTrackingService.markHabitCompletion(habit.get(), date, completed);
            System.out.println("Habit completion marked.");
        } else {
            System.out.println("Habit not found.");
        }
    }

    private void viewHabitStatisticsByPeriod(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose period (day/week/month): ");
        String period = scanner.nextLine().toLowerCase();

        Map<Habit, Long> stats = habitTrackingService.getHabitStatistics(user, period);
        stats.forEach((habit, count) -> {
            System.out.println("Habit: " + habit.getTitle() + " - Completed " + count + " times in the last " + period);
        });
    }

    private void editUserProfile(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        userService.updateUser(user, newName, newEmail, newPassword);
        System.out.println("Profile updated successfully.");
    }

    private void deleteUser(User user) {
        System.out.println("Are you sure you want to delete your account? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            userService.deleteUser(user);
            System.out.println("Account deleted.");
            // Автоматический выход после удаления аккаунта
            System.exit(0);
        } else {
            System.out.println("Account deletion canceled.");
        }
    }

    private void viewAllUsers() {
        List<User> users = userService.getAllUsers();
        users.forEach(user -> {
            System.out.println("User: " + user.getEmail() + " - " + user.getName());
            user.getHabits().forEach(habit -> {
                System.out.println("  Habit: " + habit.getTitle() + " - " + habit.getFrequency());
            });
        });
    }

    private void blockUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user email to block: ");
        String email = scanner.nextLine();
        User user = userService.findUserByEmail(email);
        if (user != null) {
            userService.blockUser(user);
            System.out.println("User " + email + " has been blocked.");
        } else {
            System.out.println("User not found.");
        }
    }
    private void deleteUserAsAdmin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user email to delete: ");
        String email = scanner.nextLine();
        userService.deleteUserAsAdmin(email);
        System.out.println("User " + email + " has been deleted.");
    }

    private void viewStreakForHabit(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter habit title to view streak: ");
        String title = scanner.nextLine();

        Habit habit = user.getHabits().stream()
                .filter(h -> h.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);

        if (habit != null) {
            long streak = habitTrackingService.calculateStreak(habit);
            System.out.println("Current streak for habit '" + habit.getTitle() + "' (" + habit.getFrequency() + "): " + streak);
        } else {
            System.out.println("Habit not found.");
        }
    }
    public static void main(String[] args) {
        new ConsoleApp().start();
    }
}