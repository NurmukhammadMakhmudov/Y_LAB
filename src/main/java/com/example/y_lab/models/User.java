package com.example.y_lab.models;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private List<Habit> habits;

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    private boolean isBlocked;

    public User(int id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.habits = new ArrayList<>();
        this.isBlocked = false;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public List<Habit> getHabits() {
        return habits;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addHabit(Habit habit) {
        habits.add(habit);
    }

    public void removeHabit(Habit habit) {
        habits.remove(habit);
    }
}