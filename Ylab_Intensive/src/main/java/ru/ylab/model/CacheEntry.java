package ru.ylab.model;

import java.util.ArrayList;
import java.util.List;

public record CacheEntry(List<Product> data, // Кэшированные данные
                         long timestamp) {// Время создания записи (в миллисекундах)

    public CacheEntry(List<Product> data, long timestamp) {
        this.data = new ArrayList<>(data.stream().toList()); // Создаём КОПИЮ списка для безопасности
        this.timestamp = timestamp;
    }

    @Override
    public List<Product> data() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("timestamp: %d, data: %s", timestamp, data);
    }
}
