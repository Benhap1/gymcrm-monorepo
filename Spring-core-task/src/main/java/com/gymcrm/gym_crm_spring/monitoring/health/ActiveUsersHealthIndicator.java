package com.gymcrm.gym_crm_spring.monitoring.health;

import com.gymcrm.gym_crm_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActiveUsersHealthIndicator implements HealthIndicator {

    private final UserService userService;

    @Override
    public Health health() {
        long activeUsers = userService.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getActive()))
                .count();

        if (activeUsers > 0) {
            return Health.up().withDetail("activeUsers", activeUsers).build();
        } else {
            return Health.down().withDetail("activeUsers", 0).build();
        }
    }
}
