package com.gymcrm.gym_crm_spring.exception;

public class TrainerNotFoundException extends RuntimeException {
    public TrainerNotFoundException(String username) {
        super("Trainer with username '" + username + "' not found");
    }
}
