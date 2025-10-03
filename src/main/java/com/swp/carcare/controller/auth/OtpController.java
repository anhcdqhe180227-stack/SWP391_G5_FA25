package com.swp.carcare.controller.auth;


import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.OwnerRepository;
import com.swp.carcare.repository.UserRepository;
import com.swp.carcare.service.EmailSenderService;
import com.swp.carcare.service.OtpService;
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
    private OtpService otpService;

    @RequestMapping(value = "otp-check", method = RequestMethod.GET)
    public String indexOtp() {
        return "auth/otpConfirm";
    }

    @RequestMapping(value = "confirm-otp", method = RequestMethod.POST)
    public String checkOtp(HttpSession session, @RequestParam("otp") String otp, Model model, RedirectAttributes redirectAttributes) {
        boolean isOtpValid = otpService.verifyOtpAndCreateUser(session, otp);

        if (isOtpValid) {
            redirectAttributes.addFlashAttribute("registrationSuccess", true);
            redirectAttributes.addFlashAttribute("userFullName", session.getAttribute("firstName") + " " + session.getAttribute("lastName"));
            redirectAttributes.addFlashAttribute("userEmail", session.getAttribute("email"));
            redirectAttributes.addFlashAttribute("userPhone", session.getAttribute("phone"));
            redirectAttributes.addFlashAttribute("userGender", session.getAttribute("gender").equals("male") ? "Nam" : "Nữ");
            redirectAttributes.addFlashAttribute("userAddress", session.getAttribute("address"));

            return "redirect:/";
        }

        model.addAttribute("mess", "OTP không chính xác! Hãy kiểm tra lại email của bạn.");
        return "auth/otpConfirm";
    }
}