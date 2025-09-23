package com.swp.carcare.service.impl;

import com.swp.carcare.config.SecurityUser;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.exception.UserBlockedException;
import com.swp.carcare.repository.UserRepository;
import com.swp.carcare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean validateCredentials(String username, String password) {
        return userRepository.existsByEmailAndPassword(username, passwordEncoder.encode(password));
    }

    @Override
    public UserEntity saveUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            System.out.println(username);
            Optional<UserEntity> userByUsername = userRepository.findByEmail(username);
            if (userByUsername.isEmpty()) {
                throw new UsernameNotFoundException("Invalid credentials!");
            }
            UserEntity user = userByUsername.get();

            if (user.getStatus() == null || user.getStatus() != 1) {
                throw new UserBlockedException("Tài khoản của bạn đã bị khóa!");
            }

            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            String role = "ROLE_";
            Integer roleUser = user.getRole();
            if (roleUser == 0) {
                role += "ADMIN";
            } else if (roleUser == 1) {
                role += "OWNER";
            } else if (roleUser == 2) {
                role += "EMP";
            } else if (roleUser == 3) {
                role += "RECEP";
            }

            grantedAuthorities.add(new SimpleGrantedAuthority(role));

            return new SecurityUser(user.getEmail(), user.getPassword(), true, true, true, true, grantedAuthorities,
                    user.getEmail());
        } catch (UserBlockedException | UsernameNotFoundException e) {
            System.out.println("Lỗi: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Lỗi không xác định: " + e.getMessage());
            throw new UsernameNotFoundException("Invalid credentials!");
        }
    }

}
