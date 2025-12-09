package com.gymcrm.gym_crm_spring.service;

import com.gymcrm.gym_crm_spring.dao.AbstractDaoJpa;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public abstract class AbstractService<T> {

    protected final AbstractDaoJpa<T> dao;

    protected AbstractService(AbstractDaoJpa<T> dao) {
        this.dao = dao;
    }

    @Transactional
    public T save(T e) {
        return dao.save(e);
    }

    @Transactional(readOnly = true)
    public Optional<T> findById(UUID id) {
        return dao.findById(id);
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return dao.findAll();
    }
    @Transactional
    public void delete(UUID id) {
        dao.delete(id);
    }
}

