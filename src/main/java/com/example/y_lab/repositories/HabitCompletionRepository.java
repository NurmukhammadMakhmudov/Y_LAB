package com.example.y_lab.repositories;

import com.example.y_lab.models.Habit;
import com.example.y_lab.models.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    List<HabitCompletion> findByHabit(Habit habit);
}
