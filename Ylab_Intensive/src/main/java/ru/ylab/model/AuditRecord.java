package main.java.ru.ylab.model;

import main.java.ru.ylab.model.enums.Action;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AuditRecord implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime timestamp;
    private final String username;
    private final Action action;
    private final String details;

    public AuditRecord(String username, Action action, String details) {
        this.timestamp = LocalDateTime.now();
        this.username = username;
        this.action = action;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public Action getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuditRecord that = (AuditRecord) o;
        return Objects.equals(timestamp, that.timestamp) && Objects.equals(username, that.username) && action == that.action && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, username, action, details);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s",
                timestamp.format(FORMATTER), username, action.getActionName(), details);
    }
}
