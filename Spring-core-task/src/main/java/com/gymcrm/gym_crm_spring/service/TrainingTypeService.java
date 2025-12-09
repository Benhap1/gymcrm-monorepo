package com.gymcrm.gym_crm_spring.service;

import com.gymcrm.gym_crm_spring.dao.TrainingTypeDao;
import com.gymcrm.gym_crm_spring.domain.TrainingType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingTypeService extends AbstractService<TrainingType> {
    private final TrainingTypeDao dao;

    public TrainingTypeService(TrainingTypeDao dao) {
        super(dao);
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public Optional<TrainingType> findByName(String name) {
        return dao.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<TrainingType> findAll() {
        return dao.findAll();
    }
}





