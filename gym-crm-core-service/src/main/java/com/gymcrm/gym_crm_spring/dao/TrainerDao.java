package com.gymcrm.gym_crm_spring.dao;

import com.gymcrm.gym_crm_spring.domain.Trainer;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public class TrainerDao extends AbstractDaoJpa<Trainer> {
    public TrainerDao() {
        super(Trainer.class);
    }

    public Optional<Trainer> findByUsername(String username) {
        return getEntityManager()
                .createQuery("select t from Trainer t where lower(t.user.username) = :u", Trainer.class)
                .setParameter("u", username.toLowerCase())
                .getResultStream()
                .findFirst();
    }

    public List<Trainer> findNotAssignedToTrainee(UUID traineeId) {
        return getEntityManager()
                .createQuery("select tr from Trainer tr where :tid not in (select ta.id from tr.assignedTrainees ta)", Trainer.class)
                .setParameter("tid", traineeId)
                .getResultList();
    }

    public Optional<Trainer> findByFirstAndLastName(String firstName, String lastName) {
        return getEntityManager()
                .createQuery("select t from Trainer t " +
                        "where lower(t.user.firstName) = :f and lower(t.user.lastName) = :l", Trainer.class)
                .setParameter("f", firstName.toLowerCase())
                .setParameter("l", lastName.toLowerCase())
                .getResultStream()
                .findFirst();
    }
}
