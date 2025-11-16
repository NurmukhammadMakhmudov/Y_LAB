package ru.ylab.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ylab.config.DatabaseConfig;
import ru.ylab.model.User;
import ru.ylab.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UserRepositoryImpl implements UserRepository {


    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    // CREATE
    @Override
    public User create(String username, String password) {
        String sql = "INSERT INTO catalog.users (username, password) VALUES (?, ?) RETURNING id, username, password, created_at";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    logger.info("User created: {}", username);
                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating user: {}", username, e);
            throw new RuntimeException("Failed to create user", e);
        }

        throw new RuntimeException("Failed to create user: no result returned");
    }

    // READ
    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT id, username, password, created_at FROM catalog.users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user: {}", username, e);
            throw new RuntimeException("Failed to find user", e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT id, username, password, created_at FROM catalog.users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID: {}", id, e);
            throw new RuntimeException("Failed to find user", e);
        }

        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, username, password, created_at FROM catalog.users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching all users", e);
            throw new RuntimeException("Failed to fetch users", e);
        }

        return users;
    }

    // UPDATE
    @Override
    public boolean update(long id, String newPassword) {
        String sql = "UPDATE catalog.users SET password = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setLong(2, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User updated: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating user: {}", id, e);
            throw new RuntimeException("Failed to update user", e);
        }

        return false;
    }

    // DELETE
    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM catalog.users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("User deleted: ID {}", id);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting user: {}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }

        return false;
    }

    // UTILITY
    @Override
    public boolean exists(String username) {
        String sql = "SELECT COUNT(*) FROM catalog.users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking user existence: {}", username, e);
            throw new RuntimeException("Failed to check user existence", e);
        }

        return false;
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM catalog.users";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting users", e);
            throw new RuntimeException("Failed to count users", e);
        }

        return 0;
    }

    // Helper method
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}