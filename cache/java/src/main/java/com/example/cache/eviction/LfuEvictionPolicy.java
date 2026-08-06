package com.example.cache.eviction;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * O(1) LFU with frequency buckets and LRU tie-breaks.
 *
 * <p>Each key has a Node storing its current frequency. A second map groups keys by frequency. Each
 * bucket is a LinkedHashSet, which keeps insertion/access order inside that frequency; the first key
 * in the lowest-frequency bucket is therefore the least-recently-used among the least-frequently-used
 * keys. minFrequency points directly to the eviction bucket, avoiding scans.
 */
public class LfuEvictionPolicy<K> implements EvictionPolicy<K> {

    private static final class Node<K> {
        private final K key;
        private int frequency;

        private Node(K key, int frequency) {
            this.key = key;
            this.frequency = frequency;
        }
    }

    private final Map<K, Node<K>> nodes = new HashMap<>();
    private final Map<Integer, LinkedHashSet<K>> frequencyBuckets = new HashMap<>();
    private int minFrequency = 0;

    @Override
    public void keyAccessed(K key) {
        Node<K> node = nodes.get(key);
        if (node == null) {
            return;
        }
        LinkedHashSet<K> oldBucket = frequencyBuckets.get(node.frequency);
        oldBucket.remove(key);
        if (oldBucket.isEmpty()) {
            frequencyBuckets.remove(node.frequency);
            if (minFrequency == node.frequency) {
                minFrequency++;
            }
        }
        node.frequency++;
        frequencyBuckets.computeIfAbsent(node.frequency, ignored -> new LinkedHashSet<>()).add(key);
    }

    @Override
    public void keyInserted(K key) {
        Node<K> node = new Node<>(key, 1);
        nodes.put(key, node);
        frequencyBuckets.computeIfAbsent(1, ignored -> new LinkedHashSet<>()).add(key);
        minFrequency = 1;
    }

    @Override
    public K evictKey() {
        LinkedHashSet<K> bucket = frequencyBuckets.get(minFrequency);
        if (bucket == null || bucket.isEmpty()) {
            return null;
        }
        K victim = bucket.iterator().next();
        bucket.remove(victim);
        if (bucket.isEmpty()) {
            frequencyBuckets.remove(minFrequency);
        }
        nodes.remove(victim);
        return victim;
    }
}
