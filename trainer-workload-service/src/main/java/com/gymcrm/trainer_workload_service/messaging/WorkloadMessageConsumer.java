package com.gymcrm.trainer_workload_service.messaging;

import com.gymcrm.trainer_workload_service.dto.TrainerWorkloadRequest;
import com.gymcrm.trainer_workload_service.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadMessageConsumer {

    private final WorkloadService workloadService;
    private final JmsTemplate jmsTemplate;

    @Value("${trainer.workload.dlq}")
    private String dlqName;

    @JmsListener(destination = "${trainer.workload.queue}")
    public void consume(TrainerWorkloadRequest request) {
        try {
            log.info("Received workload message: {}", request);
            workloadService.updateWorkload(request);
        } catch (Exception e) {
            log.error("Failed to process workload message, sending to DLQ", e);

            jmsTemplate.convertAndSend(dlqName, request, message -> {
                message.setStringProperty("_type", JmsTypes.TRAINER_WORKLOAD_V1);
                return message;
            });
        }
    }

    @JmsListener(destination = "${trainer.workload.dlq}")
    public void consumeDlq(TrainerWorkloadRequest request) {
        log.warn("Message in DLQ: {}", request);
    }
}
