package com.example.y_lab.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Habit {
    private final LocalDate creationDate;
    private int id;
    private String title;
    private String description;
    private String frequency;
    private List<HabitCompletion> completions;


    public Habit(int id, String title, String description, String frequency, LocalDate creationDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.creationDate = creationDate;
        this.completions = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<HabitCompletion> getCompletions() {
        return completions;
    }

    public void addCompletion(HabitCompletion completion) {
        completions.add(completion);
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }
}