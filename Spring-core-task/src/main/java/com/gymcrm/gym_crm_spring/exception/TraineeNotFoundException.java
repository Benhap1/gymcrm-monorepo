package com.gymcrm.gym_crm_spring.exception;

public class TraineeNotFoundException extends RuntimeException {
    public TraineeNotFoundException(String username) {
        super("Trainee with username '" + username + "' not found");
    }
}
