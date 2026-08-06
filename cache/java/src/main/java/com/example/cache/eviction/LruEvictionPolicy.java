package com.example.cache.eviction;

import java.util.HashMap;
import java.util.Map;

/**
 * O(1) LRU using the classic HashMap + doubly linked list combination.
 *
 * <p>The map gives O(1) access from key to its Node. The list stores recency: head is most recent,
 * tail is least recent. On every get/update we splice the node to the head. Eviction removes the
 * tail. This is exactly what LinkedHashMap/OrderedDict implement internally, written manually here
 * so the interview data structure is visible.
 */
public class LruEvictionPolicy<K> implements EvictionPolicy<K> {

    private static final class Node<K> {
        private final K key;
        private Node<K> prev;
        private Node<K> next;

        private Node(K key) {
            this.key = key;
        }
    }

    private final Map<K, Node<K>> nodes = new HashMap<>();
    private Node<K> head;
    private Node<K> tail;

    @Override
    public void keyAccessed(K key) {
        Node<K> node = nodes.get(key);
        if (node != null) {
            moveToHead(node);
        }
    }

    @Override
    public void keyInserted(K key) {
        Node<K> node = new Node<>(key);
        nodes.put(key, node);
        addToHead(node);
    }

    @Override
    public K evictKey() {
        if (tail == null) {
            return null;
        }
        Node<K> victim = tail;
        remove(victim);
        nodes.remove(victim.key);
        return victim.key;
    }

    private void moveToHead(Node<K> node) {
        if (node == head) {
            return;
        }
        remove(node);
        addToHead(node);
    }

    private void addToHead(Node<K> node) {
        node.prev = null;
        node.next = head;
        if (head != null) {
            head.prev = node;
        }
        head = node;
        if (tail == null) {
            tail = node;
        }
    }

    private void remove(Node<K> node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        node.prev = null;
        node.next = null;
    }
}
