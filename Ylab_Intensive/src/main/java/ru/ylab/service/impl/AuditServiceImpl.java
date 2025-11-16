package main.java.ru.ylab.service.impl;

import main.java.ru.ylab.model.AppData;
import main.java.ru.ylab.model.AuditRecord;
import main.java.ru.ylab.model.enums.Action;
import main.java.ru.ylab.service.AuditService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuditServiceImpl implements AuditService {

    private final List<AuditRecord> records;

    public AuditServiceImpl(AppData appData) {
        this.records = appData.getAuditRecords();
    }

    @Override
    public void log(String username, Action action, String details) {
        AuditRecord record = new AuditRecord(
                username,
                action,
                details
        );
        records.add(record);
    }

    @Override
    public List<AuditRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    @Override
    public List<AuditRecord> getRecordsByUser(String username) {
        return records.stream()
                .filter(r -> r.getUsername().equals(username))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditRecord> getRecordsByAction(String action) {
        return records.stream()
                .filter(r -> r.getAction().equals(Action.valueOf(action)))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditRecord> getRecordsAfter(LocalDateTime dateTime) {
        return records.stream()
                .filter(r -> r.getTimestamp().isAfter(dateTime))
                .collect(Collectors.toList());
    }

    @Override
    public int getRecordCount() {
        return records.size();
    }
}
