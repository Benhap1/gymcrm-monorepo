package com.gymcrm.gym_crm_spring.messaging;

import com.gymcrm.gym_crm_spring.dto.workload.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadMessageProducer {

    private final JmsTemplate jmsTemplate;

    @Value("${trainer.workload.queue}")
    private String queueName;

    public void sendWorkloadUpdate(TrainerWorkloadRequest request) {
        log.info("Sending workload message to queue={} payload={}", queueName, request);
        jmsTemplate.convertAndSend(queueName, request, message -> {
            message.setStringProperty("_type", JmsTypes.TRAINER_WORKLOAD_V1);
            return message;
        });
    }
}
