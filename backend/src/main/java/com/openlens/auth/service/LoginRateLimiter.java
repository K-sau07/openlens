package com.openlens.auth.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// simple in-memory rate limiter — 5 attempts per key per 15 minutes
// for production, replace with Redis-backed sliding window
@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 15 * 60 * 1000L;

    private record Window(int count, long windowStart) {}

    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {
        long now = Instant.now().toEpochMilli();
        Window current = windows.get(key);

        if (current == null || now - current.windowStart() > WINDOW_MS) {
            windows.put(key, new Window(1, now));
            return true;
        }

        if (current.count() >= MAX_ATTEMPTS) {
            return false;
        }

        windows.put(key, new Window(current.count() + 1, current.windowStart()));
        return true;
    }

    public int remainingAttempts(String key) {
        long now = Instant.now().toEpochMilli();
        Window current = windows.get(key);
        if (current == null || now - current.windowStart() > WINDOW_MS) return MAX_ATTEMPTS;
        return Math.max(0, MAX_ATTEMPTS - current.count());
    }

    public void reset(String key) {
        windows.remove(key);
    }
}
