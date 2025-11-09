package ru.ylab.tasks.task1.service;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> {

    private final int maxSize;
    private final LinkedHashMap<K, V> cache;

    public LruCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LruCache.this.maxSize;
            }
        };
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void clear() {
        cache.clear();
    }

}
