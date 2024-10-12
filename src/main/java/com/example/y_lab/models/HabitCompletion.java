package com.example.y_lab.models;

import java.time.LocalDate;

public class HabitCompletion {
    private LocalDate date;
    private boolean completed;

    public HabitCompletion(LocalDate date, boolean completed) {
        this.date = date;
        this.completed = completed;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCompleted() {
        return completed;
    }
}