package com.example.ratelimiter;

import com.example.ratelimiter.algorithms.FixedWindowLimiter;
import com.example.ratelimiter.algorithms.TokenBucketLimiter;

import java.time.Clock;
import java.time.Duration;

/** Runnable demo showing allow/deny decisions and deterministic-looking refill behavior. */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        RateLimiter tokenBucket = new TokenBucketLimiter(3, 1.0, Clock.systemUTC());
        System.out.println("TokenBucket capacity=3 refill=1/sec");
        for (int i = 1; i <= 4; i++) {
            System.out.println("client-a request " + i + " -> " + decision(tokenBucket.allow("client-a")));
        }
        Thread.sleep(1100); // demo only; tests use MutableClock, never sleep
        System.out.println("client-a after refill -> " + decision(tokenBucket.allow("client-a")));

        RateLimiter fixedWindow = new FixedWindowLimiter(2, Duration.ofSeconds(60), Clock.systemUTC());
        System.out.println("FixedWindow max=2 window=60s");
        System.out.println("client-b request 1 -> " + decision(fixedWindow.allow("client-b")));
        System.out.println("client-b request 2 -> " + decision(fixedWindow.allow("client-b")));
        System.out.println("client-b request 3 -> " + decision(fixedWindow.allow("client-b")));
    }

    private static String decision(boolean allowed) {
        return allowed ? "ALLOW" : "DENY";
    }
}
