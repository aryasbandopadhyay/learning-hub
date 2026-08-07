package com.example.hashmap;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyHashMapTest {

    private record BadHashKey(String id) {
        @Override
        public int hashCode() {
            return 42; // force every instance into the same bucket chain
        }
    }

    @Test
    void putAndGetReturnsStoredValue() {
        MyHashMap<String, Integer> map = new MyHashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(1, map.get("one"));
        assertEquals(2, map.get("two"));
        assertEquals(2, map.size());
    }

    @Test
    void overwriteExistingKeyDoesNotIncreaseSize() {
        MyHashMap<String, String> map = new MyHashMap<>();
        assertNull(map.put("name", "Alice"));
        assertEquals("Alice", map.put("name", "Alicia"));
        assertEquals("Alicia", map.get("name"));
        assertEquals(1, map.size());
    }

    @Test
    void removeDeletesKeyAndReturnsValue() {
        MyHashMap<Integer, String> map = new MyHashMap<>();
        map.put(7, "seven");
        assertEquals("seven", map.remove(7));
        assertNull(map.get(7));
        assertFalse(map.containsKey(7));
        assertEquals(0, map.size());
    }

    @Test
    void absentKeyReturnsNullAndRemoveIsNoOp() {
        MyHashMap<String, String> map = new MyHashMap<>();
        map.put("present", "value");
        assertNull(map.get("missing"));
        assertNull(map.remove("missing"));
        assertEquals(1, map.size());
    }

    @Test
    void containsKeyFindsPresentKeysEvenWhenValueIsNull() {
        MyHashMap<String, String> map = new MyHashMap<>();
        map.put("nullable", null);
        assertTrue(map.containsKey("nullable"));
        assertNull(map.get("nullable"));
    }

    @Test
    void collisionsAreResolvedBySeparateChaining() {
        MyHashMap<BadHashKey, String> map = new MyHashMap<>(2);
        BadHashKey a = new BadHashKey("a");
        BadHashKey b = new BadHashKey("b");
        BadHashKey c = new BadHashKey("c");

        map.put(a, "A");
        map.put(b, "B");
        map.put(c, "C");

        assertEquals("A", map.get(a));
        assertEquals("B", map.get(b));
        assertEquals("C", map.get(c));
        assertEquals("B", map.remove(b));
        assertEquals("A", map.get(a));
        assertEquals("C", map.get(c));
    }

    @Test
    void resizeTwiceKeepsEveryKeyRetrievable() {
        MyHashMap<Integer, String> map = new MyHashMap<>(4);
        int startCapacity = map.capacity();
        List<Integer> keys = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            keys.add(i);
            map.put(i, "value-" + i);
        }

        assertTrue(map.capacity() >= startCapacity * 4, "capacity should double at least twice");
        assertEquals(20, map.size());
        for (Integer key : keys) {
            assertEquals("value-" + key, map.get(key));
        }
    }
}
