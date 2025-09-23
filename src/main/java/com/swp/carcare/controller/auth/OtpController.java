package com.swp.carcare.controller.auth;


import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.OwnerRepository;
import com.swp.carcare.repository.UserRepository;
import com.swp.carcare.service.EmailSenderService;
import com.swp.carcare.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller

public class OtpController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailSenderService emailSenderService;
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "otp-check", method = RequestMethod.GET)
    public String indexOtp() {
        return "auth/otpConfirm";
    }

    @RequestMapping(value = "confirm-otp", method = RequestMethod.POST)
    public String checkOtp(HttpSession session, @RequestParam("otp") String otp, Model model, RedirectAttributes redirectAttributes) {
        String otpRegister = (String) session.getAttribute("otp-register");
        if (otp.equals(otpRegister)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail((String) session.getAttribute("email"));
            userEntity.setPassword(passwordEncoder.encode((String) session.getAttribute("password")));
            userEntity.setStatus(1);

            userEntity.setRole(1);

            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setUpdatedAt(LocalDateTime.now());
            userEntity.setCreatedBy("ADMIN");
            userEntity.setUpdatedBy("ADMIN");

            UserEntity save = userService.saveUser(userEntity);

            OwnerEntity owner = new OwnerEntity();
            owner.setUser(save);
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

            redirectAttributes.addFlashAttribute("registrationSuccess", true);
            redirectAttributes.addFlashAttribute("userFullName", owner.getFirstName() + " " + owner.getLastName());
            redirectAttributes.addFlashAttribute("userEmail", userEntity.getEmail());
            redirectAttributes.addFlashAttribute("userPhone", owner.getPhoneNumber());
            redirectAttributes.addFlashAttribute("userGender", owner.getGender().equals("male") ? "Nam" : "Nữ");
            redirectAttributes.addFlashAttribute("userAddress", owner.getAddress());

            return "redirect:/";
        }
        model.addAttribute("mess", "OTP không chính xác! Hãy check lại email của bạn");
        return "auth/otpConfirm";
    }

    public String otpCode() {
        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
        return String.valueOf(code);
    }


}