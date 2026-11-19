package javagrind.enterprise;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-client Token Bucket Rate Limiter using {@link ConcurrentHashMap} and monotonic
 * {@link System#nanoTime()} refill calculation — standard API Gateway protection pattern.
 */
@EnterpriseSkill(
        category = "Microservices & Resilience",
        name = "Token-Bucket API Rate Limiter",
        summary = "Per-client request throttling with ConcurrentHashMap and monotonic nanoTime token refill."
)
public final class TokenBucketRateLimiter {

    private static final class Bucket {
        private double tokens;
        private long lastRefillNanos;

        private Bucket(double initialTokens, long nowNanos) {
            this.tokens = initialTokens;
            this.lastRefillNanos = nowNanos;
        }
    }

    private final int maxBurstTokens;
    private final double refillTokensPerSecond;
    private final ConcurrentHashMap<String, Bucket> clientBuckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int maxBurstTokens, double refillTokensPerSecond) {
        if (maxBurstTokens <= 0 || refillTokensPerSecond <= 0) {
            throw new IllegalArgumentException("Burst and refill rate must be positive");
        }
        this.maxBurstTokens = maxBurstTokens;
        this.refillTokensPerSecond = refillTokensPerSecond;
    }

    public boolean tryAcquire(String clientId) {
        long now = System.nanoTime();
        Bucket bucket = clientBuckets.computeIfAbsent(
                clientId, id -> new Bucket(maxBurstTokens, now));

        synchronized (bucket) {
            double elapsedSeconds = (now - bucket.lastRefillNanos) / 1_000_000_000.0;
            if (elapsedSeconds > 0) {
                bucket.tokens = Math.min(maxBurstTokens, bucket.tokens + elapsedSeconds * refillTokensPerSecond);
                bucket.lastRefillNanos = now;
            }
            if (bucket.tokens >= 1.0) {
                bucket.tokens -= 1.0;
                return true;
            }
            return false;
        }
    }

    public void resetAll() {
        clientBuckets.clear();
    }
}
