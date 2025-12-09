package com.gymcrm.gym_crm_spring.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenStore {

    String createToken(UserDetails userDetails);

    void invalidateToken(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}
