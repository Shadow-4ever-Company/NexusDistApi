package com.gabmeula.nexusdistapi.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRateLimiter implements RateLimiter {

    private final int limit = 100;
    private final long windowMillis = 60_000;
    private final Map<String, RateLimitEntry> rateLimiteStack = new ConcurrentHashMap<>();

    @Override
    public boolean isOverLimit(String clientId) {
        long now = System.currentTimeMillis();

        RateLimitEntry entry = rateLimiteStack.compute(clientId, (key, current) -> {
            if (current == null || now - current.windowsStart > windowMillis) {
                return new RateLimitEntry(1, now);
            }
            current.count++;
            return current;

        });
        return entry.count > limit;
    }

}
