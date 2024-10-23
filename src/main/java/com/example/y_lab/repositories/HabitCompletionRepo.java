package com.example.y_lab.repositories;

import com.example.y_lab.RepositoriesInterfaces.HabitCompletionRepositoryInterface;
import com.example.y_lab.models.HabitCompletion;
import com.example.y_lab.services.ConnectionService;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class HabitCompletionRepo implements HabitCompletionRepositoryInterface {

    private final ConnectionService connectionService;

    public HabitCompletionRepo(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Override
    public void save(HabitCompletion habitCompletion) {
        String sql = "INSERT INTO habit_completions (date, completed, habit_id) VALUES (?, ?, ?)";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setDate(1, Date.valueOf(habitCompletion.getDate()));
            preparedStatement.setBoolean(2, habitCompletion.isCompleted());
            preparedStatement.setLong(3, habitCompletion.getHabit().getId());
            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(HabitCompletion entity) {

    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public HabitCompletion findById(Long aLong) {
        return null;
    }

    @Override
    public List<HabitCompletion> findAll() {
        return null;
    }
}
