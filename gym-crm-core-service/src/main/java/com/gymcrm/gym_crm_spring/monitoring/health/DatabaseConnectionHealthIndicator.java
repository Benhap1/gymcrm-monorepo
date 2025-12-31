package com.gymcrm.gym_crm_spring.monitoring.health;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionHealthIndicator implements HealthIndicator {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Health health() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return Health.up().withDetail("database", "PostgreSQL is reachable").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("database", "PostgreSQL is NOT reachable").build();
        }
    }
}
