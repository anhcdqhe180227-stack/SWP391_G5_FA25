package com.swp.carcare.service;


import com.swp.carcare.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;


public interface UserService extends UserDetailsService {
    boolean validateCredentials(String username, String password);

    UserEntity saveUser(UserEntity user);

    Optional<UserEntity> findByEmail(String email);
}

