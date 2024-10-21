package com.example.y_lab.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "habit_completions")
public class HabitCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "habit_completions_seq")
//    @SequenceGenerator(name = "habit_completions_seq", sequenceName = "habit_completions_sequence", allocationSize = 1)
    private Long id;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

}