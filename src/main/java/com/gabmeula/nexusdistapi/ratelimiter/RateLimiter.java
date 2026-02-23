package com.gabmeula.nexusdistapi.ratelimiter;

public interface RateLimiter {
    boolean isOverLimit(String clientId);
}
