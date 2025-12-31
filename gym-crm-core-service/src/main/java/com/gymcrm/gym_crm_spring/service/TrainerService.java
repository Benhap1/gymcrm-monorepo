package com.gymcrm.gym_crm_spring.service;

import com.gymcrm.gym_crm_spring.dao.TrainerDao;
import com.gymcrm.gym_crm_spring.domain.Trainee;
import com.gymcrm.gym_crm_spring.domain.Trainer;
import com.gymcrm.gym_crm_spring.dto.TraineeShortResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerProfileResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerProfileUpdateRequest;
import com.gymcrm.gym_crm_spring.dto.TrainerProfileUpdateResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerShortResponse;
import com.gymcrm.gym_crm_spring.exception.TraineeNotFoundException;
import com.gymcrm.gym_crm_spring.exception.TrainerNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TrainerService extends AbstractService<Trainer> {
    private final TrainerDao dao;
    private final TraineeService traineeService;

    public TrainerService(TrainerDao dao, TraineeService traineeService) {
        super(dao);
        this.dao = dao;
        this.traineeService = traineeService;
    }

    @Transactional(readOnly = true)
    public Optional<Trainer> findByUsername(String username) {
        return dao.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Trainer> findNotAssignedToTrainee(UUID traineeId) {
        return dao.findNotAssignedToTrainee(traineeId);
    }

    @Transactional(readOnly = true)
    public boolean existsByFirstAndLastName(String firstName, String lastName) {
        return dao.findByFirstAndLastName(firstName, lastName).isPresent();
    }

    @Transactional(readOnly = true)
    public TrainerProfileResponse getProfile(String username) {
        var trainer = dao.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        var user = trainer.getUser();

        var trainees = trainer.getAssignedTrainees().stream()
                .map(trainee -> new TraineeShortResponse(
                        trainee.getUser().getUsername(),
                        trainee.getUser().getFirstName(),
                        trainee.getUser().getLastName()
                ))
                .collect(Collectors.toList());

        return new TrainerProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                trainer.getSpecialization().getTrainingTypeName(),
                Boolean.TRUE.equals(user.getActive()),
                trainees
        );
    }

    @Transactional
    public TrainerProfileUpdateResponse updateProfile(TrainerProfileUpdateRequest request) {
        var trainer = dao.findByUsername(request.username())
                .orElseThrow(() -> new TrainerNotFoundException(request.username()));

        var user = trainer.getUser();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setActive(request.isActive());

        dao.save(trainer);

        var trainees = trainer.getAssignedTrainees().stream()
                .map(trainee -> new TraineeShortResponse(
                        trainee.getUser().getUsername(),
                        trainee.getUser().getFirstName(),
                        trainee.getUser().getLastName()
                ))
                .toList();

        return new TrainerProfileUpdateResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                trainer.getSpecialization().getTrainingTypeName(),
                Boolean.TRUE.equals(user.getActive()),
                trainees
        );
    }

    @Transactional(readOnly = true)
    public List<TrainerShortResponse> getNotAssignedActiveTrainers(String traineeUsername) {
        Trainee trainee = traineeService.findByUsername(traineeUsername)
                .orElseThrow(() -> new TraineeNotFoundException(traineeUsername));

        UUID traineeId = trainee.getId();

        return dao.findNotAssignedToTrainee(traineeId).stream()
                .filter(trainer -> Boolean.TRUE.equals(trainer.getUser().getActive()))
                .map(trainer -> new TrainerShortResponse(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization().getTrainingTypeName()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateActivationStatus(String username, boolean isActive) {
        var trainer = dao.findByUsername(username)
                .orElseThrow(() -> new TrainerNotFoundException(username));

        trainer.getUser().setActive(isActive);
        dao.save(trainer);
    }

}
