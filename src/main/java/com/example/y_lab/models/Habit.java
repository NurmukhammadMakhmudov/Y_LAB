package com.example.y_lab.models;

import java.util.ArrayList;
import java.util.List;

public class Habit {
    private int id;
    private String title;
    private String description;
    private String frequency; // daily, weekly, etc.
    private List<HabitCompletion> completions;

    public Habit(int id, String title, String description, String frequency) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.completions = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFrequency() {
        return frequency;
    }

    public List<HabitCompletion> getCompletions() {
        return completions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void addCompletion(HabitCompletion completion) {
        completions.add(completion);
    }
}