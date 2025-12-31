package com.gymcrm.gym_crm_spring.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStoreImpl implements TokenStore {

    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, Long> blacklisted = new ConcurrentHashMap<>();

    @Value("${jwt.blacklist.expiry:3600000}")
    private long blacklistExpiryMs;

    public TokenStoreImpl(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String createToken(UserDetails userDetails) {
        return jwtTokenProvider.generateToken(userDetails);
    }

    @Override
    public void invalidateToken(String token) {
        long expiry = Instant.now().toEpochMilli() + blacklistExpiryMs;
        blacklisted.put(token, expiry);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        Long exp = blacklisted.get(token);
        if (exp != null && exp > Instant.now().toEpochMilli()) {
            return false;
        }
        blacklisted.remove(token);
        return jwtTokenProvider.validateToken(token, userDetails);
    }
}