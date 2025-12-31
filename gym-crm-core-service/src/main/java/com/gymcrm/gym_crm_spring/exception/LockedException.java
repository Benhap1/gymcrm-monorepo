package com.gymcrm.gym_crm_spring.exception;

public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }
}