package com.gymcrm.gym_crm_spring.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public abstract class AbstractDaoJpa<T> {

    @PersistenceContext
    @Getter(AccessLevel.PROTECTED)
    private EntityManager entityManager;

    private final Class<T> clazz;

    protected AbstractDaoJpa(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T save(T entity) {
        T merged = entityManager.merge(entity);
        log.debug("Saved {}: {}", clazz.getSimpleName(), merged);
        return merged;
    }

    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(clazz, id));
    }

    public List<T> findAll() {
        TypedQuery<T> q = entityManager.createQuery("select e from " + clazz.getSimpleName() + " e", clazz);
        return q.getResultList();
    }

    public void delete(UUID id) {
        findById(id).ifPresent(entity -> {
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            log.debug("Deleted {} id={}", clazz.getSimpleName(), id);
        });
    }
}