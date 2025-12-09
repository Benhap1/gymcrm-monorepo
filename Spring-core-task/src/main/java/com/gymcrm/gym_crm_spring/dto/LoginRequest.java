package com.gymcrm.gym_crm_spring.dto;

public record LoginRequest(
        String username,
        String password
) {}
