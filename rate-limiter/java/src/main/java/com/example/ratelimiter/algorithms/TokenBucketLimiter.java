package com.example.ratelimiter.algorithms;

import com.example.ratelimiter.RateLimiter;
import com.example.ratelimiter.exception.InvalidRateLimitConfigurationException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Token Bucket rate limiter.
 *
 * <p>Idea: each client owns a bucket that can hold at most {@code capacity} tokens. Every request
 * first refills the bucket based on elapsed time from the injected {@link Clock}, then consumes one
 * token if present. This permits short bursts up to capacity while smoothing the average rate to
 * {@code refillTokensPerSecond}.
 *
 * <p>Concurrency: the map is concurrent only for locating a client's bucket. The mutable bucket
 * fields ({@code tokens} and {@code lastRefillTime}) are guarded by synchronizing on that one
 * bucket, so requests for different clients do not block each other, but requests for the same
 * client update refill + consume atomically.
 */
public class TokenBucketLimiter implements RateLimiter {

    private final int capacity;
    private final double refillTokensPerSecond;
    private final Clock clock;
    private final ConcurrentMap<String, BucketState> buckets = new ConcurrentHashMap<>();

    public TokenBucketLimiter(int capacity, double refillTokensPerSecond, Clock clock) {
        if (capacity <= 0) {
            throw new InvalidRateLimitConfigurationException("capacity must be positive");
        }
        if (refillTokensPerSecond <= 0.0) {
            throw new InvalidRateLimitConfigurationException("refill rate must be positive");
        }
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
        this.clock = clock;
    }

    @Override
    public boolean allow(String clientId) {
        BucketState bucket = buckets.computeIfAbsent(clientId,
                ignored -> new BucketState(capacity, clock.instant()));

        synchronized (bucket) {
            refill(bucket);
            if (bucket.tokens >= 1.0) {
                bucket.tokens -= 1.0;
                return true;
            }
            return false;
        }
    }

    private void refill(BucketState bucket) {
        Instant now = clock.instant();
        long elapsedNanos = Duration.between(bucket.lastRefillTime, now).toNanos();
        if (elapsedNanos <= 0) {
            return;
        }
        double tokensToAdd = (elapsedNanos / 1_000_000_000.0) * refillTokensPerSecond;
        bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
        bucket.lastRefillTime = now;
    }

    private static final class BucketState {
        private double tokens;
        private Instant lastRefillTime;

        private BucketState(double tokens, Instant lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
    }
}
