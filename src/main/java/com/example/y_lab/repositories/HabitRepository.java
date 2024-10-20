package com.example.y_lab.repositories;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.User;

import java.time.LocalDate;
import java.util.Optional;

public class HabitRepository {
    private int habitIdCounter = 1;

    public void addHabit(User user, String title, String description, String frequency) {
        Habit habit = new Habit(habitIdCounter++, title, description, frequency, LocalDate.now());
        user.addHabit(habit);
    }

    public void editHabit(Habit habit, String title, String description, String frequency) {
        habit.setTitle(title);
        habit.setDescription(description);
        habit.setFrequency(frequency);
    }

    public void deleteHabit(User user, Habit habit) {
        user.removeHabit(habit);
    }

    public Optional<Habit> findHabitById(User user, int habitId) {
        return user.getHabits().stream().filter(habit -> habit.getId() == habitId).findFirst();
    }
}