package com.gymcrm.gym_crm_spring.dao;

import com.gymcrm.gym_crm_spring.domain.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public class TrainingTypeDao extends AbstractDaoJpa<TrainingType> {
    public TrainingTypeDao() {
        super(TrainingType.class);
    }

    public Optional<TrainingType> findByName(String name) {
        return getEntityManager()
                .createQuery("select t from TrainingType t where t.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }
}
