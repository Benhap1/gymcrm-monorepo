package com.gymcrm.gym_crm_spring.monitoring.metrics;

import com.gymcrm.gym_crm_spring.service.TrainingService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TrainingMetrics implements ApplicationListener<ApplicationReadyEvent> {

    private final MeterRegistry meterRegistry;
    private final TrainingService trainingService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        Gauge.builder("gymcrm.trainings.total", () -> safeGet(() -> trainingService.findAll().size()))
                .description("Total number of trainings in the system")
                .register(meterRegistry);

        Gauge.builder("gymcrm.trainings.today", () -> safeGet(() ->
                        trainingService.findAll().stream()
                                .filter(t -> LocalDate.now().equals(t.getTrainingDate()))
                                .count()))
                .description("Number of trainings created today")
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