package com.example.y_lab.services;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.HabitCompletion;
import com.example.y_lab.models.User;
import com.example.y_lab.repositories.HabitCompletionRepo;
import com.example.y_lab.repositories.HabitRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HabitTrackingService {

    private final ConnectionService connectionService = new ConnectionService();
    private final HabitRepo habitRepo = new HabitRepo(connectionService);
    private final HabitCompletionRepo habitCompletionRepo = new HabitCompletionRepo(connectionService);



    public void markHabitCompletion(Habit habit, LocalDate date, boolean completed) {
        HabitCompletion completion = new HabitCompletion();
        completion.setCompleted(completed);
        completion.setDate(date);
        completion.setHabit(habit);
        habitCompletionRepo.save(completion);
    }

    public Map<Habit, Long> getHabitStatistics(User user, String period) {
        LocalDate now = LocalDate.now();
        List<Habit> habits = habitRepo.findByUser(user);
        return habits.stream().collect(Collectors.toMap(
                habit -> habit,
                habit -> {
                    LocalDate startDate = calculateStartDate(now, period, habit.getCreationDate());

                    return habit.getCompletions().stream()
                            .filter(c -> !c.getDate().isBefore(startDate) && c.getDate().isBefore(now.plusDays(1)))
                            .filter(HabitCompletion::isCompleted)
                            .count();
                }
        ));
    }

    private LocalDate calculateStartDate(LocalDate now, String period, LocalDate createdDate) {
        LocalDate startDate = switch (period.toLowerCase()) {
            case "day" -> now;
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> throw new IllegalArgumentException("Invalid period. Choose 'day', 'week', or 'month'.");
        };
        if (createdDate.isAfter(startDate)) {
            startDate = createdDate;
        }
        return startDate;
    }

    public long calculateStreak(Habit habit) {
        List<HabitCompletion> completions = habit.getCompletions().stream()
                .filter(HabitCompletion::isCompleted)
                .sorted(Comparator.comparing(HabitCompletion::getDate).reversed()).toList();

        if (completions.isEmpty()) {
            return 0;
        }

        LocalDate now = LocalDate.now();
        long streak = 0;
        LocalDate previousDate = now;

        for (HabitCompletion completion : completions) {
            switch (habit.getFrequency()) {
                case "daily":
                    if (completion.getDate().equals(previousDate) || completion.getDate().equals(previousDate.minusDays(1))) {
                        streak++;
                        previousDate = completion.getDate();
                    } else {
                        return streak;  
                    }
                    break;

                case "weekly":
                    if (completion.getDate().isAfter(previousDate.minusWeeks(1)) && completion.getDate().isBefore(previousDate.plusDays(1))) {
                        streak++;
                        previousDate = previousDate.minusWeeks(1);
                    } else {
                        return streak;
                    }
                    break;

                case "monthly":
                    if (completion.getDate().isAfter(previousDate.minusMonths(1)) && completion.getDate().isBefore(previousDate.plusDays(1))) {
                        streak++;
                        previousDate = previousDate.minusMonths(1);
                    } else {
                        return streak;
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported frequency: " + habit.getFrequency());
            }
        }

        return streak;
    }
}