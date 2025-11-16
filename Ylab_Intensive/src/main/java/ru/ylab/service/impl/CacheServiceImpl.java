package ru.ylab.service.impl;



import ru.ylab.model.CacheEntry;
import ru.ylab.model.Product;
import ru.ylab.service.CacheService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CacheServiceImpl implements CacheService {

    private final Map<String, CacheEntry> cache;
    private final long ttlMillis;
    private final int maxSize;

    private int hits = 0;
    private int misses = 0;
    private long totalQueryTimeNs = 0;

    public CacheServiceImpl(long ttlMillis, int maxSize) {
        this.cache = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxSize;
            }
        };
        this.ttlMillis = ttlMillis;
        this.maxSize = maxSize;
    }

    public CacheServiceImpl() {
        this(1000 * 60 * 5, 100); // 5 минут, 100 записей
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public long getTtlMillis() {
        return ttlMillis;
    }

    @Override
    public List<Product> get(String key) {
        long start = System.nanoTime();

        CacheEntry entry = cache.get(key);

        if (entry == null) {
            misses++;
            totalQueryTimeNs += (System.nanoTime() - start);
            return null;
        }

        // Проверка срока действия
        if (System.currentTimeMillis() - entry.timestamp() > ttlMillis) {
            cache.remove(key);
            misses++;
            totalQueryTimeNs += (System.nanoTime() - start);
            return null;
        }

        hits++;
        totalQueryTimeNs += (System.nanoTime() - start);
        return entry.data();
    }



    @Override
    public void put(String key, List<Product> data) {
        cache.put(key, new CacheEntry(data, System.currentTimeMillis()));
    }

    @Override
    public void invalidate(String key) {
        cache.remove(key);
    }

    @Override
    public void invalidateAll() {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public int getHits() {
        return hits;
    }

    @Override
    public int getMisses() {
        return misses;
    }

    @Override
    public double getHitRatio() {
        return (hits + misses) == 0 ? 0 : (double) hits / (hits + misses);
    }

    @Override
    public double getAverageQueryTimeMs() {
        return (hits + misses) == 0 ? 0 : totalQueryTimeNs / 1_000_000.0 / (hits + misses);
    }

}
