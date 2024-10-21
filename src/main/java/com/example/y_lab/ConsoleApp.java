package com.example.y_lab;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.User;
import com.example.y_lab.repositories.HabitRepository;
import com.example.y_lab.services.AuthenticationService;
import com.example.y_lab.services.HabitService;
import com.example.y_lab.services.HabitTrackingService;
import com.example.y_lab.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class ConsoleApp implements CommandLineRunner {

    private final AuthenticationService authenticationService;
    private final HabitService habitService;
    private final UserService userService;

    @Autowired
    public ConsoleApp(AuthenticationService authenticationService, HabitService habitService, UserService userService) {
        this.authenticationService = authenticationService;
        this.habitService = habitService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        start();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> authenticationService.register();
                case 2 -> {
                    Optional<User> user = authenticationService.login();
                    if (user.isEmpty())
                        return;
                    userMenu(user.get());
                }
                case 3 -> System.exit(0);
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void userMenu(User user) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("""
                    1. Create Habit
                    2. View Habits
                    3. Edit Habit
                    4. Delete Habit
                    5. Mark Habit Completion
                    6. View Habit Details
                    7. View Habit Statistics
                    8. View Streak for Habit
                    9. Edit Profile
                    10. Delete Account
                    11. View All Users and Habits (Admin)
                    12. Block User (Admin)
                    13. Delete User (Admin)
                    14. Logout
                   """);
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> habitService.createHabit(user);
                case 2 -> habitService.viewHabits(user);
                case 3 -> habitService.editHabit(user);
                case 4 -> habitService.deleteHabit(user);
                case 5 -> habitService.markCompletion(user);
                case 6 -> habitService.viewHabitDetails(user);
                case 7 -> habitService.viewHabitStatisticsByPeriod(user);
                case 8 -> habitService.viewStreakForHabit(user);
                case 9 -> userService.editUserProfile(user);
                case 10 -> userService.deleteUser(user);
                case 11 -> userService.viewAllUsers();
                case 12 -> userService.blockUser();
                case 13 -> userService.deleteUserAsAdmin();
                case 14 -> {
                    return; // Logout
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}