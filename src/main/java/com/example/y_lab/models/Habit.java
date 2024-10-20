package com.example.y_lab.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "habit_seq")
    @SequenceGenerator(name = "habit_seq", sequenceName = "habi_sequence", allocationSize = 1)
    private Long id;
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String frequency;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<HabitCompletion> completions;

    public void addCompletion(HabitCompletion completion) {
        completions.add(completion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Habit habit = (Habit) o;
        return id != null && Objects.equals(id, habit.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}