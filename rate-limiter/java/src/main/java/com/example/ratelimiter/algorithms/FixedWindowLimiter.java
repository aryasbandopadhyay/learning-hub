package com.example.ratelimiter.algorithms;

import com.example.ratelimiter.RateLimiter;
import com.example.ratelimiter.exception.InvalidRateLimitConfigurationException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Fixed Window rate limiter.
 *
 * <p>Idea: time is split into equal windows. A client may make {@code maxRequestsPerWindow}
 * requests inside the current window; the counter resets when the clock crosses the next boundary.
 * It is easy to reason about and cheap to store, but it can allow a boundary burst (N requests at
 * the end of one window and N more at the start of the next).
 *
 * <p>Concurrency: just like TokenBucket, each client's mutable counter is synchronized separately,
 * making check + increment + reset atomic for that client without serializing unrelated clients.
 */
public class FixedWindowLimiter implements RateLimiter {

    private final int maxRequestsPerWindow;
    private final Duration windowSize;
    private final Clock clock;
    private final ConcurrentMap<String, WindowState> windows = new ConcurrentHashMap<>();

    public FixedWindowLimiter(int maxRequestsPerWindow, Duration windowSize, Clock clock) {
        if (maxRequestsPerWindow <= 0) {
            throw new InvalidRateLimitConfigurationException("max requests must be positive");
        }
        if (windowSize == null || windowSize.isZero() || windowSize.isNegative()) {
            throw new InvalidRateLimitConfigurationException("window size must be positive");
        }
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSize = windowSize;
        this.clock = clock;
    }

    @Override
    public boolean allow(String clientId) {
        long currentWindow = currentWindowNumber();
        WindowState state = windows.computeIfAbsent(clientId, ignored -> new WindowState(currentWindow));

        synchronized (state) {
            if (state.windowNumber != currentWindow) {
                state.windowNumber = currentWindow;
                state.count = 0;
            }
            if (state.count < maxRequestsPerWindow) {
                state.count++;
                return true;
            }
            return false;
        }
    }

    private long currentWindowNumber() {
        Instant now = clock.instant();
        return Math.floorDiv(now.toEpochMilli(), windowSize.toMillis());
    }

    private static final class WindowState {
        private long windowNumber;
        private int count;

        private WindowState(long windowNumber) {
            this.windowNumber = windowNumber;
            this.count = 0;
        }
    }
}
