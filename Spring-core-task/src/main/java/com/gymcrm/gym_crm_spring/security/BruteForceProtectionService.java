package com.gymcrm.gym_crm_spring.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class BruteForceProtectionService {

    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();
    private final Map<String, Long> lockUntil = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_DURATION_MS = TimeUnit.MINUTES.toMillis(5);

    public boolean isLocked(String username) {
        Long lockTime = lockUntil.get(username);
        long now = Instant.now().toEpochMilli();
        boolean locked = lockTime != null && now < lockTime;

        if (lockTime != null && !locked) {
            lockUntil.remove(username);
            failedAttempts.remove(username);
        }

        return locked;
    }

    public void recordFailedAttempt(String username) {
        if (!isLocked(username)) {
            int attempts = failedAttempts.merge(username, 1, Integer::sum);
            if (attempts >= MAX_ATTEMPTS) {
                long lockExpiry = Instant.now().toEpochMilli() + LOCK_DURATION_MS;
                lockUntil.put(username, lockExpiry);
                failedAttempts.remove(username);
            }
        }
    }

    public void resetAttempts(String username) {
        failedAttempts.remove(username);
        lockUntil.remove(username);
    }
}