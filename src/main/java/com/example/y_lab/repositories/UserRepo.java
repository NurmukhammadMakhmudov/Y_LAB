package com.example.y_lab.repositories;

import com.example.y_lab.RepositoriesInterfaces.UserRepositoryInterface;
import com.example.y_lab.models.User;
import com.example.y_lab.services.ConnectionService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepo implements UserRepositoryInterface {

    private final ConnectionService connectionService;

    public UserRepo(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }


    @Override
    public  void save(User user)
    {
        final String INSERT_USER_SQL = "INSERT INTO users (email, password, username, is_blocked) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_SQL))
        {
            preparedStatement.setString(1,user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3,user.getName());
            preparedStatement.setBoolean(4, user.isBlocked());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET email = ?, password = ?, username = ?, is_blocked = ? WHERE id = ?";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setBoolean(4, user.isBlocked());
            preparedStatement.setLong(5, user.getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findById(Long id) {

        final String sqlSelect = "Select * from users where id = ?";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect))
        {
            preparedStatement.setLong(1, id);
            return getUser(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User getUser(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet = preparedStatement.executeQuery();
        User user = new User();
        if (resultSet.next())
        {
            user.setId(resultSet.getLong("id"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            user.setBlocked(resultSet.getBoolean("is_blocked"));
            user.setName(resultSet.getString("username"));
        }
        else
            return null;
        connectionService.getConnection().commit();
        resultSet.close();
        return user;
    }


    @Override
    public User findByEmail(String email)
    {
        final String sqlSelect = "Select * from users where email = ?";
        try (Connection connection = connectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect))
        {
            preparedStatement.setString(1, email);
            return getUser(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll()

    {
        final String sql = "SELECT * FROM users";
        List<User> userList = new ArrayList<>();

        try ( Connection connection = connectionService.getConnection();
                Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)){

            while (resultSet.next())
            {
                User user = new User();
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setBlocked(resultSet.getBoolean("is_blocked"));
                user.setName(resultSet.getString("username"));
                user.setId(resultSet.getLong("id"));
                userList.add(user);
            }
            return userList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



    @Override
    public void delete(Long id) {
        final String sql = "DELETE FROM users WHERE id = ?";
        try ( Connection connection =connectionService.getConnection();
                PreparedStatement preparedStatement =connection.prepareStatement(sql)){
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

}
