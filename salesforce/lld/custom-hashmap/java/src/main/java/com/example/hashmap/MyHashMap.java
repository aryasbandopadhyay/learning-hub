package com.example.hashmap;

import java.util.Objects;

/**
 * A small generic HashMap built from first principles for machine-coding interviews.
 *
 * <p>The implementation intentionally avoids {@code java.util.HashMap}. Storage is an array of
 * buckets; each bucket is the head of a singly linked list. Keys that map to the same bucket are
 * kept in that linked list (separate chaining). When the load factor crosses the threshold, the
 * bucket array doubles and every existing node is re-bucketed.
 */
public class MyHashMap<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    /** One node in a bucket chain. */
    private static final class Node<K, V> {
        private final K key;
        private V value;
        private Node<K, V> next;

        private Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Node<K, V>[] buckets;
    private int size;
    private final double loadFactor;

    public MyHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public MyHashMap(int initialCapacity, double loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be positive");
        }
        if (loadFactor <= 0.0 || Double.isNaN(loadFactor)) {
            throw new IllegalArgumentException("loadFactor must be positive");
        }
        this.buckets = (Node<K, V>[]) new Node[nextPowerOfTwo(initialCapacity)];
        this.loadFactor = loadFactor;
    }

    /** Insert or overwrite a key. Returns the old value, or null when the key was new. */
    public V put(K key, V value) {
        int index = indexFor(key, buckets.length);
        for (Node<K, V> current = buckets[index]; current != null; current = current.next) {
            if (Objects.equals(current.key, key)) {
                V old = current.value;
                current.value = value;
                return old;
            }
        }

        ensureCapacityFor(size + 1);
        index = indexFor(key, buckets.length); // capacity may have changed, so recompute bucket
        buckets[index] = new Node<>(key, value, buckets[index]);
        size++;
        return null;
    }

    /** Return the value for key, or null when absent. */
    public V get(K key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    /** Remove key if present. Returns the removed value, or null when absent. */
    public V remove(K key) {
        int index = indexFor(key, buckets.length);
        Node<K, V> previous = null;
        Node<K, V> current = buckets[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    public int size() {
        return size;
    }

    /** Exposed only for demos/tests to show that resizing happened. */
    public int capacity() {
        return buckets.length;
    }

    private Node<K, V> findNode(K key) {
        int index = indexFor(key, buckets.length);
        for (Node<K, V> current = buckets[index]; current != null; current = current.next) {
            if (Objects.equals(current.key, key)) {
                return current;
            }
        }
        return null;
    }

    private void ensureCapacityFor(int targetSize) {
        if (targetSize <= buckets.length * loadFactor) {
            return;
        }
        resize(buckets.length * 2);
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Node<K, V>[] oldBuckets = buckets;
        buckets = (Node<K, V>[]) new Node[newCapacity];

        // Rehash every node because a doubled power-of-two capacity changes bucket indexes.
        for (Node<K, V> head : oldBuckets) {
            Node<K, V> current = head;
            while (current != null) {
                Node<K, V> next = current.next;
                int index = indexFor(current.key, newCapacity);
                current.next = buckets[index];
                buckets[index] = current;
                current = next;
            }
        }
    }

    private int indexFor(K key, int capacity) {
        int h = key == null ? 0 : key.hashCode();
        h ^= (h >>> 16); // spread high bits into low bits before masking
        return h & (capacity - 1); // valid because capacity is always a power of two
    }

    private static int nextPowerOfTwo(int value) {
        int capacity = 1;
        while (capacity < value) {
            capacity <<= 1;
        }
        return capacity;
    }
}
