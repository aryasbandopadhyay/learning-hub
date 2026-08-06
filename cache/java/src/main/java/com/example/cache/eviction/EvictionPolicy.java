package com.example.cache.eviction;

/**
 * Strategy interface for eviction bookkeeping.
 *
 * <p>The Cache owns the key->value map and delegates only ordering/frequency state to this policy.
 * That separation is the Strategy pattern: adding FIFO or Random later means implementing this
 * interface, not editing Cache's get/put logic.
 */
public interface EvictionPolicy<K> {
    /** Called when an existing key is read or updated. */
    void keyAccessed(K key);

    /** Called after a brand-new key is admitted to the cache. */
    void keyInserted(K key);

    /** Pick and remove the victim from policy state; Cache removes the actual value. */
    K evictKey();
}
