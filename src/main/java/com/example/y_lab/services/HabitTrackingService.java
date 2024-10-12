package com.example.y_lab.services;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.HabitCompletion;

import java.time.LocalDate;

public class HabitTrackingService {

    public void markHabitCompletion(Habit habit, LocalDate date, boolean completed) {
        HabitCompletion completion = new HabitCompletion(date, completed);
        habit.addCompletion(completion);
    }

    public long calculateStreak(Habit habit) {
        return habit.getCompletions().stream()
                .filter(HabitCompletion::isCompleted)
                .count();
    }
}