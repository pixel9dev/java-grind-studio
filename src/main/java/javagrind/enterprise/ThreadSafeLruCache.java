package javagrind.enterprise;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Production-style bounded Thread-Safe LRU Cache backed by an access-ordered
 * {@link LinkedHashMap} and guarded by a {@link ReentrantReadWriteLock}.
 *
 * Note: Because `LinkedHashMap.get()` in access-order mode mutates the internal
 * doubly-linked list order, `get()` acquires the write lock while read-only
 * `size()` / `snapshotStats()` use lock-free atomics or read locks!
 */
@EnterpriseSkill(
        category = "Concurrency & Data Structures",
        name = "Thread-Safe Generic LRU Cache",
        summary = "Bounded LinkedHashMap(accessOrder=true) with ReentrantReadWriteLock and AtomicLong hit/miss telemetry."
)
public final class ThreadSafeLruCache<K, V> {

    public record CacheStats(int size, int capacity, long hits, long misses, long evictions) {
        public double hitRatioPercent() {
            long total = hits + misses;
            return total == 0 ? 0.0 : (hits * 100.0) / total;
        }
    }

    private final int capacity;
    private final Map<K, V> store;
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong evictions = new AtomicLong();

    public ThreadSafeLruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.capacity = capacity;
        this.store = new LinkedHashMap<>(capacity, 0.75f, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                boolean evict = size() > ThreadSafeLruCache.this.capacity;
                if (evict) {
                    evictions.incrementAndGet();
                }
                return evict;
            }
        };
    }

    public Optional<V> get(K key) {
        rwLock.writeLock().lock();
        try {
            if (store.containsKey(key)) {
                hits.incrementAndGet();
                return Optional.ofNullable(store.get(key));
            }
            misses.incrementAndGet();
            return Optional.empty();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void put(K key, V value) {
        rwLock.writeLock().lock();
        try {
            store.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public void clear() {
        rwLock.writeLock().lock();
        try {
            store.clear();
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public CacheStats stats() {
        rwLock.readLock().lock();
        try {
            return new CacheStats(store.size(), capacity, hits.get(), misses.get(), evictions.get());
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
