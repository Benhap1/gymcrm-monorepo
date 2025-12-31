package com.gymcrm.gym_crm_spring.service;

import com.gymcrm.gym_crm_spring.dao.TraineeDao;
import com.gymcrm.gym_crm_spring.dao.TrainerDao;
import com.gymcrm.gym_crm_spring.domain.Trainee;
import com.gymcrm.gym_crm_spring.dto.TraineeProfileResponse;
import com.gymcrm.gym_crm_spring.dto.TraineeProfileUpdateRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeProfileUpdateResponse;
import com.gymcrm.gym_crm_spring.dto.TraineeTrainerListUpdateRequest;
import com.gymcrm.gym_crm_spring.dto.TraineeTrainerListUpdateResponse;
import com.gymcrm.gym_crm_spring.dto.TraineeTrainingResponse;
import com.gymcrm.gym_crm_spring.dto.TrainerShortResponse;
import com.gymcrm.gym_crm_spring.exception.TraineeNotFoundException;
import com.gymcrm.gym_crm_spring.exception.TrainerNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TraineeService extends AbstractService<Trainee> {
    private final TraineeDao dao;
    private final TrainerDao trainerDao;
    private final TrainingService trainingService;

    public TraineeService(TraineeDao dao, TrainerDao trainerDao, TrainingService trainingService) {
        super(dao);
        this.dao = dao;
        this.trainerDao = trainerDao;
        this.trainingService = trainingService;
    }

    @Transactional(readOnly = true)
    public Optional<Trainee> findByUsername(String username) {
        return dao.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByFirstAndLastName(String firstName, String lastName) {
        return dao.findByFirstAndLastName(firstName, lastName).isPresent();
    }

    @Transactional(readOnly = true)
    public TraineeProfileResponse getProfile(String username) {
        var trainee = dao.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        var user = trainee.getUser();

        var trainers = trainee.getAssignedTrainers().stream()
                .map(trainer -> new TrainerShortResponse(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization().getTrainingTypeName()
                ))
                .toList();

        return new TraineeProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                user.getActive(),
                trainers
        );
    }


    @Transactional
    public TraineeProfileUpdateResponse updateProfile(TraineeProfileUpdateRequest request) {
        var trainee = dao.findByUsername(request.username())
                .orElseThrow(() -> new TraineeNotFoundException(request.username()));

        var user = trainee.getUser();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setActive(request.isActive());

        trainee.setDateOfBirth(request.dateOfBirth().orElse(null));
        trainee.setAddress(request.address().orElse(null));

        dao.save(trainee);

        var trainers = trainee.getAssignedTrainers().stream()
                .map(trainer -> new TrainerShortResponse(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization().getTrainingTypeName()
                ))
                .toList();

        return new TraineeProfileUpdateResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                Boolean.TRUE.equals(user.getActive()),
                trainers
        );
    }

    @Transactional
    public void deleteByUsername(String username) {
        var trainee = dao.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));
        delete(trainee.getId());
    }

    @Transactional
    public TraineeTrainerListUpdateResponse updateTrainerList(TraineeTrainerListUpdateRequest request) {
        var trainee = dao.findByUsername(request.traineeUsername())
                .orElseThrow(() -> new TraineeNotFoundException(request.traineeUsername()));

        var trainers = request.trainersUsernames().stream()
                .map(username -> trainerDao.findByUsername(username)
                        .orElseThrow(() -> new TrainerNotFoundException(username)))
                .collect(Collectors.toSet());

        trainee.setAssignedTrainers(trainers);
        dao.save(trainee);

        var trainersResponse = trainers.stream()
                .map(trainer -> new TrainerShortResponse(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization().getTrainingTypeName()
                ))
                .toList();

        return new TraineeTrainerListUpdateResponse(trainersResponse);
    }

    @Transactional(readOnly = true)
    public List<TraineeTrainingResponse> getTraineeTrainings(
            String username,
            LocalDate from,
            LocalDate to,
            String trainerName,
            String trainingType
    ) {
        var trainee = dao.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        var trainings = trainingService.findByCriteriaForTrainee(
                username, from, to, trainerName, trainingType
        );

        return trainings.stream()
                .map(tr -> new TraineeTrainingResponse(
                        tr.getTrainingName(),
                        tr.getTrainingDate(),
                        tr.getTrainingType().getTrainingTypeName(),
                        tr.getTrainingDuration(),
                        tr.getTrainer().getUser().getUsername()
                ))
                .toList();
    }

    @Transactional
    public void updateActivationStatus(String username, boolean isActive) {
        var trainee = dao.findByUsername(username)
                .orElseThrow(() -> new TraineeNotFoundException(username));

        trainee.getUser().setActive(isActive);
        dao.save(trainee);
    }


}


