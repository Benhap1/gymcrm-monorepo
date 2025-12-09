package com.gymcrm.gym_crm_spring.config;

import com.gymcrm.gym_crm_spring.dto.workload.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${trainer.workload-service.url}")
    private String workloadServiceUrl;

    public void updateWorkload(TrainerWorkloadRequest request, String jwtToken) {
        String transactionId = MDC.get("transactionId");

        webClientBuilder.build().post()
                .uri(workloadServiceUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .header("X-Transaction-Id", transactionId)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .doOnError(ex -> handleError(request, ex))
                .subscribe();

        log.info("Sent workload update request for trainer: {}", request.getUsername());
    }

    private void handleError(TrainerWorkloadRequest request, Throwable t) {
        log.error("Failed to update workload for trainer {}. Reason: {}",
                request.getUsername(), t.getMessage());
    }
}