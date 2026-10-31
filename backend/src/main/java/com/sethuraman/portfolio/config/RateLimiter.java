package com.sethuraman.portfolio.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple in-memory rate limiter for login and contact form endpoints.
 * <p>
 * For production, replace with Redis-backed rate limiting (e.g., Bucket4j + Redis).
 * This implementation uses a sliding window per client identifier.
 * </p>
 */
@Component
public class RateLimiter {

    // Max requests per window
    private static final int MAX_CONTACT_REQUESTS = 5;
    private static final int MAX_LOGIN_ATTEMPTS = 10;

    // Window duration in milliseconds
    private static final long CONTACT_WINDOW_MS = 60_000;    // 1 minute
    private static final long LOGIN_WINDOW_MS   = 300_000;   // 5 minutes

    private final Map<String, RequestWindow> contactWindows = new ConcurrentHashMap<>();
    private final Map<String, RequestWindow> loginWindows   = new ConcurrentHashMap<>();

    /**
     * Returns true if the request should be allowed (not rate-limited).
     */
    public boolean allowContactRequest(String clientKey) {
        return checkAndRecord(contactWindows, clientKey, MAX_CONTACT_REQUESTS, CONTACT_WINDOW_MS);
    }

    public boolean allowLoginAttempt(String clientKey) {
        return checkAndRecord(loginWindows, clientKey, MAX_LOGIN_ATTEMPTS, LOGIN_WINDOW_MS);
    }

    private boolean checkAndRecord(Map<String, RequestWindow> windows, String key,
                                   int maxRequests, long windowMs) {
        long now = System.currentTimeMillis();
        RequestWindow window = windows.compute(key, (k, existing) -> {
            if (existing == null || now - existing.windowStart > windowMs) {
                return new RequestWindow(now);
            }
            return existing;
        });

        int count = window.count.incrementAndGet();
        return count <= maxRequests;
    }

    // ---- Inner class ----

    private static class RequestWindow {
        final long windowStart;
        final AtomicInteger count = new AtomicInteger(0);

        RequestWindow(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}
