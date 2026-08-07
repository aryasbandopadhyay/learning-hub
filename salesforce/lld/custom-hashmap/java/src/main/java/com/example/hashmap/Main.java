package com.example.hashmap;

/** Runnable deterministic demo for the custom hash map. */
public class Main {

    public static void main(String[] args) {
        MyHashMap<Integer, String> map = new MyHashMap<>(4);

        System.out.println("Initial capacity: " + map.capacity());
        map.put(1, "Alice");
        System.out.println("After put(1, Alice): " + map.get(1));
        map.put(2, "Bob");
        System.out.println("After put(2, Bob): " + map.get(2));
        map.put(1, "Alicia");
        System.out.println("After overwrite put(1, Alicia): " + map.get(1));
        System.out.println("Contains key 2: " + map.containsKey(2));
        System.out.println("Remove key 2: " + map.remove(2));
        System.out.println("Contains key 2: " + map.containsKey(2));
        System.out.println("Size after remove: " + map.size());

        for (int i = 0; i < 10; i++) {
            map.put(100 + i, "V" + i);
        }
        boolean allRetrievable = true;
        for (int i = 0; i < 10; i++) {
            allRetrievable &= ("V" + i).equals(map.get(100 + i));
        }
        System.out.println("Capacity after resizing demo: " + map.capacity());
        System.out.println("All resize keys retrievable: " + allRetrievable);
    }
}
