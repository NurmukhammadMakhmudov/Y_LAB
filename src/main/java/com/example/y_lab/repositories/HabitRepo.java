package com.example.y_lab.repositories;

import com.example.y_lab.RepositoriesInterfaces.HabitRepositoryInterface;
import com.example.y_lab.models.Habit;
import com.example.y_lab.models.HabitCompletion;
import com.example.y_lab.models.User;
import com.example.y_lab.services.ConnectionService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitRepo implements HabitRepositoryInterface {

    private final ConnectionService connectionService;

    public HabitRepo(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Override
    public void save(Habit habit) {
        final String INSERT_USER_SQL = "INSERT INTO habits (creation_date, title, description, frequency,user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL)) {
            preparedStatement.setDate(1, Date.valueOf(habit.getCreationDate()));
            preparedStatement.setString(2, habit.getTitle());
            preparedStatement.setString(3, habit.getDescription());
            preparedStatement.setString(4, habit.getFrequency());
            preparedStatement.setLong(5, habit.getUser().getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        habit.setId(generatedKeys.getLong(1));
                    }
                }
            }
            if (habit.getCompletions() != null) {
                for (HabitCompletion completion : habit.getCompletions()) {
                    completion.setHabit(habit);
                    saveCompletion(completion);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Habit habit) {
        final String UPDATE_USER_SQL = "UPDATE  habits SET creation_date = ?, title = ?, description = ?, frequency = ? WHERE id = ?";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_SQL)) {
            preparedStatement.setDate(1, Date.valueOf(habit.getCreationDate()));
            preparedStatement.setString(2, habit.getTitle());
            preparedStatement.setString(3, habit.getDescription());
            preparedStatement.setString(4, habit.getFrequency());
            preparedStatement.setLong(5, habit.getId());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        habit.setId(generatedKeys.getLong(1));
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        final String sql = "DELETE FROM habits WHERE id = ?";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            preparedStatement.setLong(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0)
                connection.commit();
            else
                connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Habit findById(Long id) {
        final String selectHabit = "SELECT * FROM habits WHERE id = ?";
        final String selectAllHabitCompletions = "SELECT * FROM habit_completions WHERE habit_id = ?";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement habitStatement = connection.prepareStatement(selectHabit);
             PreparedStatement completionStatement = connection.prepareStatement(selectAllHabitCompletions)
        ) {
            habitStatement.setLong(1, id);
            try (ResultSet habitResultSet = habitStatement.executeQuery()) {
                Habit habit = new Habit();
                if (habitResultSet.next()) {
                    habit.setCreationDate(habitResultSet.getDate("creation_date").toLocalDate());
                    habit.setFrequency(habitResultSet.getString("frequency"));
                    habit.setTitle(habitResultSet.getString("title"));
                    habit.setDescription(habitResultSet.getString("description"));
                    habit.setId(habitResultSet.getLong("id"));
                    completionStatement.setLong(1, habit.getId());
                    List<HabitCompletion> habitCompletions = new ArrayList<>();
                    try (ResultSet completionsResultSet = completionStatement.executeQuery()) {
                        while (completionsResultSet.next()) {
                            HabitCompletion habitCompletion = new HabitCompletion();
                            habitCompletion.setCompleted(completionsResultSet.getBoolean("completed"));
                            habitCompletion.setDate(completionsResultSet.getDate("date").toLocalDate());
                            habitCompletion.setId(completionsResultSet.getLong("id"));
                            habitCompletions.add(habitCompletion);
                        }
                    }
                    habit.setCompletions(habitCompletions);
                } else
                    return null;
                connection.commit();
                return habit;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Habit> findAll() {
        final String sqlSelect = "Select * from habits";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect)) {
            List<Habit> habits = new ArrayList<>();
            try (ResultSet resultSet = preparedStatement.executeQuery()
            ) {
                while (resultSet.next()) {
                    Habit habit = new Habit();
                    habit.setCreationDate(resultSet.getDate("creation_date").toLocalDate());
                    habit.setFrequency(resultSet.getString("frequency"));
                    habit.setTitle(resultSet.getString("title"));
                    habit.setId(resultSet.getLong("id"));
                    habits.add(habit);
                }
            }


            connection.commit();
            return habits;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveCompletion(HabitCompletion completion) {
        String sql = "INSERT INTO habit_completions (habit_id, completed, date) VALUES (?, ?, ?)";

        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, completion.getHabit().getId());
            preparedStatement.setBoolean(2, true);
            preparedStatement.setDate(3, Date.valueOf(completion.getDate()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Habit> findByUser(User user) {
        final String selectHabit = "SELECT * FROM habits WHERE user_id = ?";
        final String selectAllHabitCompletions = "SELECT * FROM habit_completions WHERE habit_id = ?";

        try (Connection connection = connectionService.getConnection();
             PreparedStatement habitStatement = connection.prepareStatement(selectHabit);
             PreparedStatement completionStatement = connection.prepareStatement(selectAllHabitCompletions)) {
            List<Habit> habits = new ArrayList<>();
            habitStatement.setLong(1, user.getId());
            try (ResultSet habitsResultSet = habitStatement.executeQuery()) {

                while (habitsResultSet.next()) {
                    List<HabitCompletion> completions = new ArrayList<>();

                    Habit habit = new Habit();
                    habit.setId(habitsResultSet.getLong("id"));
                    habit.setCreationDate(habitsResultSet.getDate("creation_date").toLocalDate());
                    habit.setFrequency(habitsResultSet.getString("frequency"));
                    habit.setTitle(habitsResultSet.getString("title"));
                    habit.setDescription(habitsResultSet.getString("description"));
                    completionStatement.setLong(1, habit.getId());

                    try (ResultSet completionsResultSet = completionStatement.executeQuery()) {
                        while (completionsResultSet.next()) {
                            HabitCompletion habitCompletion = new HabitCompletion();
                            habitCompletion.setCompleted(completionsResultSet.getBoolean("completed"));
                            habitCompletion.setId(completionsResultSet.getLong("id"));
                            habitCompletion.setDate(completionsResultSet.getDate("date").toLocalDate());
                            completions.add(habitCompletion);
                        }
                    }
                    habit.setCompletions(completions);
                    habits.add(habit);
                }


            }

            return habits;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
