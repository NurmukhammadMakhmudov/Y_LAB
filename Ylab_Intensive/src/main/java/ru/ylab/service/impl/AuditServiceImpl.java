package ru.ylab.service.impl;

import ru.ylab.model.AuditRecord;
import ru.ylab.model.enums.Action;
import ru.ylab.repository.AuditRepository;
import ru.ylab.service.AuditService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of AuditService using database storage
 */
public class AuditServiceImpl implements AuditService {
    private final AuditRepository auditRepository;

    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void log(String username, Action action, String details) {
        auditRepository.create(username, action, details);
    }

    @Override
    public List<AuditRecord> getAllRecords() {
        return auditRepository.findAll();
    }

    @Override
    public List<AuditRecord> getRecordsByUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return auditRepository.findByUsername(username);
    }

    @Override
    public List<AuditRecord> getRecordsByAction(String action) {
        return auditRepository.findByAction(action);
    }

    @Override
    public List<AuditRecord> getRecordsAfter(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime cannot be null");
        }
        return auditRepository.findAfterTimestamp(dateTime);
    }

    @Override
    public int getRecordCount() {
        return auditRepository.count();
    }
}