package com.example.cache;

import com.example.cache.eviction.EvictionPolicy;
import com.example.cache.exception.InvalidCapacityException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Generic in-memory cache with pluggable eviction.
 *
 * <p>The cache stores values in a HashMap and delegates all admission/access/eviction ordering to an
 * EvictionPolicy. get/put/size/containsKey are guarded by one ReentrantLock. This coarse lock keeps
 * the MVP easy to reason about: the value map and policy metadata always change atomically together,
 * so size never exceeds capacity even when many threads hammer put/get concurrently.
 */
public class Cache<K, V> {

    private final int capacity;
    private final EvictionPolicy<K> evictionPolicy;
    private final Map<K, V> values = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public Cache(int capacity, EvictionPolicy<K> evictionPolicy) {
        if (capacity <= 0) {
            throw new InvalidCapacityException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.evictionPolicy = evictionPolicy;
    }

    /** Return Optional.empty() on miss; a hit updates recency/frequency through the policy. */
    public Optional<V> get(K key) {
        lock.lock();
        try {
            if (!values.containsKey(key)) {
                return Optional.empty();
            }
            evictionPolicy.keyAccessed(key);
            return Optional.ofNullable(values.get(key));
        } finally {
            lock.unlock();
        }
    }

    /** Insert or update. If full, evict one victim selected by the policy before admitting a new key. */
    public void put(K key, V value) {
        lock.lock();
        try {
            if (values.containsKey(key)) {
                values.put(key, value);
                evictionPolicy.keyAccessed(key); // update refreshes LRU and increments LFU frequency
                return;
            }
            if (values.size() >= capacity) {
                K victim = evictionPolicy.evictKey();
                if (victim != null) {
                    values.remove(victim);
                }
            }
            values.put(key, value);
            evictionPolicy.keyInserted(key);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return values.size();
        } finally {
            lock.unlock();
        }
    }

    public int capacity() {
        return capacity;
    }

    /** Membership check intentionally does not update recency/frequency; only get/put are accesses. */
    public boolean containsKey(K key) {
        lock.lock();
        try {
            return values.containsKey(key);
        } finally {
            lock.unlock();
        }
    }
}
