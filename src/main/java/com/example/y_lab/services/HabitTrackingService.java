package com.example.y_lab.services;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.HabitCompletion;
import com.example.y_lab.models.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HabitTrackingService {

    public void markHabitCompletion(Habit habit, LocalDate date, boolean completed) {
        HabitCompletion completion = new HabitCompletion(date, completed);
        habit.addCompletion(completion);
    }

    public Map<Habit, Long> getHabitStatistics(User user, String period) {
        LocalDate now = LocalDate.now();

        return user.getHabits().stream().collect(Collectors.toMap(
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
        LocalDate startDate;
        switch (period.toLowerCase()) {
            case "day":
                startDate = now;
                break;
            case "week":
                startDate = now.minusWeeks(1);
                break;
            case "month":
                startDate = now.minusMonths(1);
                break;
            default:
                throw new IllegalArgumentException("Invalid period. Choose 'day', 'week', or 'month'.");
        }
        if (createdDate.isAfter(startDate)) {
            startDate = createdDate;
        }
        return startDate;
    }

    public long calculateStreak(Habit habit) {
        List<HabitCompletion> completions = habit.getCompletions().stream()
                .filter(HabitCompletion::isCompleted)  // Фильтруем только выполненные привычки
                .sorted(Comparator.comparing(HabitCompletion::getDate).reversed())  // Сортируем по дате от последней
                .collect(Collectors.toList());

        if (completions.isEmpty()) {
            return 0;  // Если привычка никогда не выполнялась
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
                        return streak;  // Серия прерывается
                    }
                    break;

                case "weekly":
                    if (completion.getDate().isAfter(previousDate.minusWeeks(1)) && completion.getDate().isBefore(previousDate.plusDays(1))) {
                        streak++;
                        previousDate = previousDate.minusWeeks(1);
                    } else {
                        return streak;  // Серия прерывается
                    }
                    break;

                case "monthly":
                    if (completion.getDate().isAfter(previousDate.minusMonths(1)) && completion.getDate().isBefore(previousDate.plusDays(1))) {
                        streak++;
                        previousDate = previousDate.minusMonths(1);
                    } else {
                        return streak;  // Серия прерывается
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported frequency: " + habit.getFrequency());
            }
        }

        return streak;
    }
}