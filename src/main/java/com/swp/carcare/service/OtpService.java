package com.swp.carcare.service;

import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.OwnerRepository;
import com.swp.carcare.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OtpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean verifyOtpAndCreateUser(HttpSession session, String otp) {
        String otpRegister = (String) session.getAttribute("otp-register");

        if (otp.equals(otpRegister)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail((String) session.getAttribute("email"));
            userEntity.setPassword(passwordEncoder.encode((String) session.getAttribute("password")));
            userEntity.setStatus(1);
            userEntity.setRole(1); // Set default role to 1
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setUpdatedAt(LocalDateTime.now());
            userEntity.setCreatedBy("ADMIN");
            userEntity.setUpdatedBy("ADMIN");

            UserEntity savedUser = userRepository.save(userEntity);

            OwnerEntity owner = new OwnerEntity();
            owner.setUser(savedUser);
            owner.setStatus(1);
            owner.setPhoneNumber((String) session.getAttribute("phone"));
            owner.setFirstName((String) session.getAttribute("firstName"));
            owner.setLastName((String) session.getAttribute("lastName"));
            owner.setAddress((String) session.getAttribute("address"));
            owner.setCreatedAt(LocalDateTime.now());
            owner.setUpdatedAt(LocalDateTime.now());
            owner.setCreatedBy("ADMIN");
            owner.setUpdatedBy("ADMIN");
            owner.setGender((String) session.getAttribute("gender"));

            ownerRepository.save(owner);
            return true;
        }

        return false;
    }
}
