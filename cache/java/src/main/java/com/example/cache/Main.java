package com.example.cache;

import com.example.cache.eviction.LfuEvictionPolicy;
import com.example.cache.eviction.LruEvictionPolicy;

/** Runnable demo showing LRU and LFU eviction. */
public class Main {
    public static void main(String[] args) {
        Cache<String, String> lru = new Cache<>(2, new LruEvictionPolicy<>());
        lru.put("a", "Alpha");
        lru.put("b", "Beta");
        System.out.println("LRU get a: " + lru.get("a").orElse("MISS"));
        lru.put("c", "Gamma");
        System.out.println("LRU contains a: " + lru.containsKey("a"));
        System.out.println("LRU contains b: " + lru.containsKey("b"));
        System.out.println("LRU contains c: " + lru.containsKey("c"));

        Cache<String, String> lfu = new Cache<>(2, new LfuEvictionPolicy<>());
        lfu.put("a", "Alpha");
        lfu.put("b", "Beta");
        lfu.get("a");
        lfu.get("a");
        lfu.get("b");
        lfu.put("c", "Gamma");
        System.out.println("LFU contains a: " + lfu.containsKey("a"));
        System.out.println("LFU contains b: " + lfu.containsKey("b"));
        System.out.println("LFU contains c: " + lfu.containsKey("c"));
    }
}
