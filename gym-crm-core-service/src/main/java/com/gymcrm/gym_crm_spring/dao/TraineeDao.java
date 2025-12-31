package com.gymcrm.gym_crm_spring.dao;

import com.gymcrm.gym_crm_spring.domain.Trainee;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public class TraineeDao extends AbstractDaoJpa<Trainee> {
    public TraineeDao() {
        super(Trainee.class);
    }

    public Optional<Trainee> findByUsername(String username) {
        TypedQuery<Trainee> q = getEntityManager().createQuery("""
            select distinct t from Trainee t
            left join fetch t.user
            left join fetch t.assignedTrainers tr
            left join fetch tr.user
            left join fetch tr.specialization
            where lower(t.user.username) = :u
            """, Trainee.class);
        q.setParameter("u", username.toLowerCase());
        return q.getResultStream().findFirst();
    }

    public Optional<Trainee> findByFirstAndLastName(String firstName, String lastName) {
        TypedQuery<Trainee> q = getEntityManager()
                .createQuery("select t from Trainee t where lower(t.user.firstName) = :f and lower(t.user.lastName) = :l", Trainee.class)
                .setParameter("f", firstName.toLowerCase())
                .setParameter("l", lastName.toLowerCase());
        return q.getResultStream().findFirst();
    }
}
