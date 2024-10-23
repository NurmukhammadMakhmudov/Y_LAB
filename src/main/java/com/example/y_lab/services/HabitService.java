package com.example.y_lab.services;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.User;
import com.example.y_lab.repositories.HabitRepo;
import com.example.y_lab.repositories.HabitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;


@Service
public class HabitService {

    private final Scanner scanner = new Scanner(System.in);

    private final ConnectionService connectionService = new ConnectionService();
    private final HabitRepo habitRepo = new HabitRepo(connectionService);
    private final HabitTrackingService habitTrackingService;

    public HabitService(HabitTrackingService habitTrackingService) {
        this.habitTrackingService = habitTrackingService;
    }

    public void createHabit(User user) {
        Habit habit = new Habit();
        System.out.print("Title: ");
        habit.setTitle(scanner.nextLine());
        System.out.print("Description: ");
        habit.setDescription(scanner.nextLine());
        System.out.print("Frequency (daily/weekly): ");
        habit.setFrequency(scanner.nextLine());
        habit.setUser(user);
        habit.setCreationDate(LocalDate.now());
        habitRepo.save(habit);
        System.out.println("Habit created.");
    }

    public void viewHabits(User user) {
        System.out.println("Filter by frequency (daily/weekly) or press Enter to see all: ");
        String filter = scanner.nextLine().toLowerCase();
        List<Habit> list = habitRepo.findByUser(user);
        list.stream()
                .filter(habit -> filter.isEmpty() || habit.getFrequency().toLowerCase().equals(filter))
                .forEach(habit -> System.out.println(habit.getId() + ". " + habit.getTitle() + " (" + habit.getFrequency() + ")"));
    }
    public void viewHabitDetails(User user) {
        viewHabits(user);
        System.out.print("Enter habit ID to view details: ");
        long id = scanner.nextInt();
        scanner.nextLine();

         Habit habit = habitRepo.findById(id);
        if (habit != null) {
            System.out.println("Title: " + habit.getTitle());
            System.out.println("Description: " + habit.getDescription());
            System.out.println("Frequency: " + habit.getFrequency());
            System.out.println("Completions:");
            habit.getCompletions().forEach(c -> System.out.println("Date: " + c.getDate() + ", Completed: " + c.isCompleted()));
        } else {
            System.out.println("Habit not found.");
        }
    }

    public void editHabit(User user) {
        viewHabits(user);
        System.out.print("Enter habit ID to edit: ");
        long id = scanner.nextLong();
        scanner.nextLine();
        Habit habit = habitRepo.findById(id);
        if (habit != null) {
            habit.setUser(user);
            System.out.print("New Title: ");
            habit.setTitle(scanner.nextLine());
            System.out.print("New Description: ");
            habit.setDescription(scanner.nextLine());
            System.out.print("New Frequency (daily/weekly): ");
            habit.setFrequency(scanner.nextLine());
            habitRepo.update(habit);
            System.out.println("Habit updated.");
        } else {
            System.out.println("Habit not found.");
        }
    }

    public void deleteHabit(User user) {
        viewHabits(user);
        System.out.print("Enter habit ID to delete: ");
        long id = scanner.nextLong();
        Habit habit = habitRepo.findById(id);
        if (habit != null) {
            habitRepo.delete(habit.getId());
            System.out.println("Habit deleted.");
        } else {
            System.out.println("Habit not found.");
        }
    }

    public void markCompletion(User user) {
        viewHabits(user);
        System.out.print("Enter habit ID to mark completion: ");
        long id = scanner.nextLong();
        scanner.nextLine();
        Habit habit = habitRepo.findById(id);
        if (habit != null) {
            System.out.print("Completion date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            if (date.isAfter(LocalDate.now())) {
                System.out.println("Error: Completion date cannot be in the future.");
                return;
            }

            System.out.print("Completed? (true/false): ");
            boolean completed = scanner.nextBoolean();
            habitTrackingService.markHabitCompletion(habit, date, completed);
            System.out.println("Habit completion marked.");
        } else {
            System.out.println("Habit not found.");
        }
    }

    public void viewHabitStatisticsByPeriod(User user) {
        System.out.print("Choose period (day/week/month): ");
        String period = scanner.nextLine().toLowerCase();

        Map<Habit, Long> stats = habitTrackingService.getHabitStatistics(user, period);
        stats.forEach((habit, count) -> System.out.println("Habit: " + habit.getTitle() + " - Completed " + count + " times in the last " + period));
    }
    public void viewStreakForHabit(User user) {
        viewHabits(user);
        System.out.print("Enter habit ID to mark completion: ");
        long id = scanner.nextLong();
        scanner.nextLine();
        Habit habit = habitRepo.findById(id);


        if (habit != null) {
            long streak = habitTrackingService.calculateStreak(habit);
            System.out.println("Current streak for habit '" + habit.getTitle() + "' (" + habit.getFrequency() + "): " + streak);
        } else {
            System.out.println("Habit not found.");
        }
    }

}
