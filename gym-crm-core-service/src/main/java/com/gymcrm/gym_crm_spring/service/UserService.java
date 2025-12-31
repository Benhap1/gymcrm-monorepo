package com.gymcrm.gym_crm_spring.service;

import com.gymcrm.gym_crm_spring.dao.UserDao;
import com.gymcrm.gym_crm_spring.domain.User;
import com.gymcrm.gym_crm_spring.exception.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService extends AbstractService<User> {

    private final PasswordEncoder passwordEncoder;
    private final UserDao userDao;

    public UserService(PasswordEncoder passwordEncoder, UserDao userDao) {
        super(userDao);
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        var user = userDao.findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(user);
    }
}
