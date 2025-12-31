package com.gymcrm.gym_crm_spring.service;

import com.gymcrm.gym_crm_spring.dao.TrainingDao;
import com.gymcrm.gym_crm_spring.domain.Training;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class TrainingService extends AbstractService<Training> {
    private final TrainingDao dao;


    public TrainingService(TrainingDao dao) {
        super(dao);
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public List<Training> findByCriteriaForTrainee(String traineeUsername,
                                                   LocalDate from,
                                                   LocalDate to,
                                                   String trainerName,
                                                   String trainingType) {
        return dao.findByCriteriaForTrainee(traineeUsername, from, to, trainerName, trainingType);
    }

    @Transactional(readOnly = true)
    public List<Training> findByCriteriaForTrainer(String trainerUsername,
                                                   LocalDate from,
                                                   LocalDate to,
                                                   String traineeName) {
        return dao.findByCriteriaForTrainer(trainerUsername, from, to, traineeName);
    }

    @Transactional
    public void saveTraining(Training training) {
        dao.save(training);
    }

}


