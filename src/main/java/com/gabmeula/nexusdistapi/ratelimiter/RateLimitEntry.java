package com.gabmeula.nexusdistapi.ratelimiter;

public class RateLimitEntry {
    int count;
    long windowsStart;

    public RateLimitEntry(int count, long windowsStart) {
        this.count = count;
        this.windowsStart = windowsStart;
    }

}