package com.example.y_lab;

import com.example.y_lab.models.User;
import com.example.y_lab.services.HabitService;
import com.example.y_lab.services.AuthenticationService;
import com.example.y_lab.repositories.HabitRepository;
import com.example.y_lab.services.HabitTrackingService;
import com.example.y_lab.services.UserService;

import java.util.Scanner;

public class ConsoleApp {
    private final UserService userService = new UserService();
    private final HabitRepository habitRepository = new HabitRepository();
    private final HabitTrackingService habitTrackingService = new HabitTrackingService();
    private final AuthenticationService authenticationService = new AuthenticationService(userService, this);
    private final HabitService habitService = new HabitService(habitRepository, habitTrackingService);


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
                case 2 -> authenticationService.login();
                case 3 -> System.exit(0);
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
                case 1:
                    habitService.createHabit(user);
                    break;
                case 2:
                    habitService.viewHabits(user);
                    break;
                case 3:
                    habitService.editHabit(user);
                    break;
                case 4:
                    habitService.deleteHabit(user);
                    break;
                case 5:
                    habitService.markCompletion(user);
                    break;
                case 6:
                    habitService.viewHabitDetails(user);
                    break;
                case 7:
                    habitService.viewHabitStatisticsByPeriod(user);
                    break;
                case 8:
                    habitService.viewStreakForHabit(user);
                    break;
                case 9:
                    userService.editUserProfile(user);
                    break;
                case 10:
                    userService.deleteUser(user);
                    break;
                case 11:
                    userService.viewAllUsers();
                    break;
                case 12:
                    userService.blockUser();
                    break;
                case 13:
                    userService.deleteUserAsAdmin();
                    break;
                case 14:
                    return;
            }
        }
    }


    public static void main(String[] args) {
        new ConsoleApp().start();
    }
}