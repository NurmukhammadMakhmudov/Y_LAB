package com.example.y_lab.services;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.User;
import com.example.y_lab.repositories.HabitRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitTrackingService habitTrackingService;

    public HabitService(HabitRepository habitRepository, HabitTrackingService habitTrackingService) {
        this.habitRepository = habitRepository;
        this.habitTrackingService = habitTrackingService;
    }

    public void createHabit(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Frequency (daily/weekly): ");
        String frequency = scanner.nextLine();
        habitRepository.addHabit(user, title, description, frequency);
        System.out.println("Habit created.");
    }

    public void viewHabits(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Filter by frequency (daily/weekly) or press Enter to see all: ");
        String filter = scanner.nextLine().toLowerCase();

        user.getHabits().stream()
                .filter(habit -> filter.isEmpty() || habit.getFrequency().toLowerCase().equals(filter))
                .forEach(habit -> System.out.println(habit.getId() + ". " + habit.getTitle() + " (" + habit.getFrequency() + ")"));
    }
    public void viewHabitDetails(User user) {
        Scanner scanner = new Scanner(System.in);
        viewHabits(user);
        System.out.print("Enter habit ID to view details: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Optional<Habit> habit = habitRepository.findHabitById(user, id);
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

    public void editHabit(User user) {
        Scanner scanner = new Scanner(System.in);
        viewHabits(user);
        System.out.print("Enter habit ID to edit: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        Optional<Habit> habit = habitRepository.findHabitById(user, id);
        if (habit.isPresent()) {
            System.out.print("New Title: ");
            String title = scanner.nextLine();
            System.out.print("New Description: ");
            String description = scanner.nextLine();
            System.out.print("New Frequency (daily/weekly): ");
            String frequency = scanner.nextLine();
            habitRepository.editHabit(habit.get(), title, description, frequency);
            System.out.println("Habit updated.");
        } else {
            System.out.println("Habit not found.");
        }
    }

    public void deleteHabit(User user) {
        Scanner scanner = new Scanner(System.in);
        viewHabits(user);
        System.out.print("Enter habit ID to delete: ");
        int id = scanner.nextInt();
        Optional<Habit> habit = habitRepository.findHabitById(user, id);
        if (habit.isPresent()) {
            habitRepository.deleteHabit(user, habit.get());
            System.out.println("Habit deleted.");
        } else {
            System.out.println("Habit not found.");
        }
    }

    public void markCompletion(User user) {
        Scanner scanner = new Scanner(System.in);
        viewHabits(user);
        System.out.print("Enter habit ID to mark completion: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline
        Optional<Habit> habit = habitRepository.findHabitById(user, id);
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

    public void viewHabitStatisticsByPeriod(User user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose period (day/week/month): ");
        String period = scanner.nextLine().toLowerCase();

        Map<Habit, Long> stats = habitTrackingService.getHabitStatistics(user, period);
        stats.forEach((habit, count) -> {
            System.out.println("Habit: " + habit.getTitle() + " - Completed " + count + " times in the last " + period);
        });
    }
    public void viewStreakForHabit(User user) {
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

}
