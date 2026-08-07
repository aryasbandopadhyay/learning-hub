package com.example.pool;

import com.example.pool.model.Connection;
import com.example.pool.service.ConnectionPool;

import java.time.Duration;

/** Runnable demo: borrow, use, release, and show deterministic availability changes. */
public class Main {

    public static void main(String[] args) {
        ConnectionPool pool = new ConnectionPool(2);

        System.out.println("Pool size: " + pool.size());
        System.out.println("Available at start: " + pool.available());

        Connection first = pool.borrow(Duration.ofMillis(100));
        System.out.println("Borrowed " + first.getId());
        System.out.println("Available after first borrow: " + pool.available());

        Connection second = pool.borrow(Duration.ofMillis(100));
        System.out.println("Borrowed " + second.getId());
        System.out.println("Available after second borrow: " + pool.available());

        pool.release(first);
        System.out.println("Released " + first.getId());
        System.out.println("Available after release: " + pool.available());

        Connection again = pool.borrow(Duration.ofMillis(100));
        System.out.println("Borrowed again " + again.getId());
        pool.release(second);
        pool.release(again);
        System.out.println("Available at end: " + pool.available());
    }
}
