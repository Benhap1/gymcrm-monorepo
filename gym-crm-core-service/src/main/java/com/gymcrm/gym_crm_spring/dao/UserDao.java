package com.gymcrm.gym_crm_spring.dao;

import com.gymcrm.gym_crm_spring.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDao extends AbstractDaoJpa<User> {
    public UserDao() {
        super(User.class);
    }

    public Optional<User> findByUsername(String username) {
        return getEntityManager()
                .createQuery("select u from User u where lower(u.username)=:u", User.class)
                .setParameter("u", username.toLowerCase())
                .getResultStream()
                .findFirst();
    }
}