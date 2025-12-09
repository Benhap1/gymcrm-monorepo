package com.gymcrm.gym_crm_spring.monitoring.metrics;

import com.gymcrm.gym_crm_spring.service.UserService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMetrics implements ApplicationListener<ApplicationReadyEvent> {

    private final MeterRegistry meterRegistry;
    private final UserService userService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        Gauge.builder("gymcrm.users.total", () -> safeGet(() -> userService.findAll().size()))
                .description("Total number of registered users")
                .register(meterRegistry);

        Gauge.builder("gymcrm.users.active", () -> safeGet(() ->
                        userService.findAll().stream()
                                .filter(u -> Boolean.TRUE.equals(u.getActive()))
                                .count()))
                .description("Number of active users")
                .register(meterRegistry);
    }

    private double safeGet(SupplierWithException<Number> supplier) {
        try {
            return supplier.get().doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    @FunctionalInterface
    interface SupplierWithException<T> {
        T get() throws Exception;
    }
}