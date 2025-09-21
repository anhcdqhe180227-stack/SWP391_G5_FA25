package com.swp.carcare.service;


import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    boolean validateCredentials(String username, String password);
}

