package com.example.ratelimiter;

/**
 * Public abstraction for a per-client rate limiter.
 *
 * <p>This is the Strategy seam: callers depend on this tiny interface, while the concrete algorithm
 * can be Token Bucket, Fixed Window, Sliding Window, or a future Redis-backed implementation. The
 * method returns a boolean instead of throwing because throttling is an expected business decision,
 * not an exceptional error.
 */
public interface RateLimiter {

    /**
     * Decide whether one request for {@code clientId} should pass right now.
     *
     * @return true when the request is within the configured limit; false when it is throttled.
     */
    boolean allow(String clientId);
}
