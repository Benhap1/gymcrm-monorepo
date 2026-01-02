package com.gymcrm.gym_crm_spring.security;

import com.gymcrm.gym_crm_spring.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TrainingSecurity {

    private final TrainingService trainingService;

    public boolean isTrainerOwner(UUID trainingId, String username) {
        return trainingService.findById(trainingId)
                .map(t -> t.getTrainer().getUser().getUsername())
                .filter(owner -> owner.equals(username))
                .isPresent();
    }
}
