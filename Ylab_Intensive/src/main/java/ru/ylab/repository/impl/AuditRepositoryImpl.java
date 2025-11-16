package ru.ylab.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ylab.config.DatabaseConfig;
import ru.ylab.model.AuditRecord;
import ru.ylab.model.enums.Action;
import ru.ylab.repository.AuditRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AuditRepositoryImpl implements AuditRepository {
    private static final Logger logger = LoggerFactory.getLogger(AuditRepositoryImpl.class);

    // CREATE
    public AuditRecord create(String username, Action action, String details) {
        String sql = "INSERT INTO audit.audit_records (username, action, details, timestamp) " +
                "VALUES (?, ?, ?, ?) RETURNING id, username, action, details, timestamp";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, action.toString());
            stmt.setString(3, details);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AuditRecord record = mapResultSetToAuditRecord(rs);
                    logger.debug("Audit log created: {} - {}", username, action.getActionName());
                    return record;
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating audit record", e);
            throw new RuntimeException("Failed to create audit record", e);
        }

        throw new RuntimeException("Failed to create audit record: no result returned");
    }

    // READ
    public List<AuditRecord> findAll() {
        String sql = "SELECT id, username, action, details, timestamp " +
                "FROM audit.audit_records ORDER BY timestamp DESC";
        List<AuditRecord> records = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                records.add(mapResultSetToAuditRecord(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching audit records", e);
            throw new RuntimeException("Failed to fetch audit records", e);
        }

        return records;
    }

    public List<AuditRecord> findByUsername(String username) {
        String sql = "SELECT id, username, action, details, timestamp " +
                "FROM audit.audit_records WHERE username = ? ORDER BY timestamp DESC";
        List<AuditRecord> records = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToAuditRecord(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching audit records for user: {}", username, e);
            throw new RuntimeException("Failed to fetch audit records", e);
        }

        return records;
    }

    public List<AuditRecord> findByAction(String action) {
        String sql = "SELECT id, username, action, details, timestamp " +
                "FROM audit.audit_records WHERE action = ? ORDER BY timestamp DESC";
        List<AuditRecord> records = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, action);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToAuditRecord(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching audit records by action: {}", action, e);
            throw new RuntimeException("Failed to fetch audit records", e);
        }

        return records;
    }

    public List<AuditRecord> findAfterTimestamp(LocalDateTime timestamp) {
        String sql = "SELECT id, username, action, details, timestamp " +
                "FROM audit.audit_records WHERE timestamp > ? ORDER BY timestamp DESC";
        List<AuditRecord> records = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(timestamp));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(mapResultSetToAuditRecord(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching audit records after: {}", timestamp, e);
            throw new RuntimeException("Failed to fetch audit records", e);
        }

        return records;
    }

    // UTILITY
    public int count() {
        String sql = "SELECT COUNT(*) FROM audit.audit_records";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting audit records", e);
            throw new RuntimeException("Failed to count audit records", e);
        }

        return 0;
    }

    public void deleteOlderThan(LocalDateTime timestamp) {
        String sql = "DELETE FROM audit.audit_records WHERE timestamp < ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(timestamp));
            int deleted = stmt.executeUpdate();
            logger.info("Deleted {} old audit records", deleted);
        } catch (SQLException e) {
            logger.error("Error deleting old audit records", e);
            throw new RuntimeException("Failed to delete audit records", e);
        }
    }

    // Helper method
    private AuditRecord mapResultSetToAuditRecord(ResultSet rs) throws SQLException {
        String d = rs.getString("action");
        return new AuditRecord(
                rs.getLong("id"),
                rs.getString("username"),
                Action.valueOf(d.toUpperCase()),
                rs.getString("details"),
                rs.getTimestamp("timestamp").toLocalDateTime()
        );
    }
}