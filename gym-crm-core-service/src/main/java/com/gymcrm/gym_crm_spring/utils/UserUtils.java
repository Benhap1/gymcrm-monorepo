package com.gymcrm.gym_crm_spring.utils;

import com.gymcrm.gym_crm_spring.domain.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class UserUtils {
    private final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final SecureRandom RANDOM = new SecureRandom();
    private final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public String generateUsername(String firstName, String lastName, List<? extends User> existingUsers) {
        String f = firstName == null ? "" : firstName.trim().toLowerCase();
        String l = lastName == null ? "" : lastName.trim().toLowerCase();
        String base = f + "." + l;
        Set<String> existing = new HashSet<>();
        if (existingUsers != null) {
            for (User u : existingUsers) {
                if (u != null && u.getUsername() != null) existing.add(u.getUsername().toLowerCase());
            }
        }
        if (!existing.contains(base)) return base;
        int i = 1;
        while (existing.contains(base + i)) i++;
        return base + i;
    }

    public String generatePassword(int length) {
        if (length <= 0) throw new IllegalArgumentException("Password length must be >0");
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(PASSWORD_CHARS.charAt(RANDOM.nextInt(PASSWORD_CHARS.length())));
        return sb.toString();
    }

    public String generatePassword() { return generatePassword(10); }

    public String encodePassword(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }
}
