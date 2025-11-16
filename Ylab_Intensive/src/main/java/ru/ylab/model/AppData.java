package main.java.ru.ylab.model;


import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AppData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Все данные приложения
    private final Map<Integer, Product> products;
    private final Map<String, User> users;
    private final List<AuditRecord> auditRecords;
    private int nextProductId;

    public AppData() {
        this.products = new HashMap<>();
        this.users = new HashMap<>();
        this.auditRecords = new ArrayList<>();
        this.nextProductId = 1;
    }

    public Map<Integer, Product> getProducts() {
        return products;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public List<AuditRecord> getAuditRecords() {
        return auditRecords;
    }

    public int getNextProductId() {
        return nextProductId;
    }

    public void setNextProductId(int nextProductId) {
        this.nextProductId = nextProductId;
    }

    @Override
    public String toString() {
        return String.format("AppData{products=%d, users=%d, auditRecords=%d, nextProductId=%d}",
                products.size(), users.size(), auditRecords.size(), nextProductId);
    }
}

