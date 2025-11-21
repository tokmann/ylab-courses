package ru.ylab.tasks.task2.service;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Реализация простого LRU-кеша (Least Recently Used).
 * Кеш автоматически удаляет самые старые элементы при переполнении.
 * Используется для ускорения поиска товаров с одинаковыми фильтрами.
 *
 * @param <K> тип ключа (например, FilterKey)
 * @param <V> тип значения (например, List<Product>)
 */
public class LruCache<K, V> {

    private final int maxSize;
    private final LinkedHashMap<K, V> cache;


    /**
     * Конструктор создает LinkedHashMap с флагом accessOrder = true,
     * чтобы при каждом обращении к элементу он перемещался в конец списка.
     * При превышении maxSize самый старый элемент будет удален автоматически.
     */
    public LruCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            // Переопределяем метод для удаления самого старого элемента при переполнении
            @Override
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
