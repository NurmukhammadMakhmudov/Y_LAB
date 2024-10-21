package com.example.y_lab.repositories;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    Optional<Habit> findById(long id);
    List<Habit>  findByUser(User user);

//    private int habitIdCounter = 1;
//
//    public void addHabit(User user, String title, String description, String frequency) {
//        Habit habit = new Habit();
//        habit.setTitle(title);
//        habit.setDescription(description);
//        habit.setFrequency(frequency);
//        user.addHabit(habit);
//    }
//
//    public void editHabit(Habit habit, String title, String description, String frequency) {
//        habit.setTitle(title);
//        habit.setDescription(description);
//        habit.setFrequency(frequency);
//    }
//
//    public void deleteHabit(User user, Habit habit) {
//        user.removeHabit(habit);
//    }
//
//    public Optional<Habit> findHabitById(User user, int habitId) {
//        return user.getHabits().stream().filter(habit -> habit.getId() == habitId).findFirst();
//    }
}