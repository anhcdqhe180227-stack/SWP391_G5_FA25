package com.swp.carcare.controller.auth;

import com.swp.carcare.service.EmailSenderService;
import com.swp.carcare.service.UserService;
import com.swp.carcare.repository.OwnerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLogin(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        return "auth/login";
    }

    @GetMapping("/404")
    public String notFound() {
        return "auth/404";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/?logout";
    }

    @RequestMapping(value = "register")
    public String addUser() {
        return "auth/register";
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(@RequestParam String email, @RequestParam String firstName, @RequestParam String lastName,
                       @RequestParam String address, @RequestParam String gender,
                       @RequestParam String phone, @RequestParam String password, Model model, HttpSession session) {

        String validationMessage = userService.validateUserInput(email, firstName, lastName, address, gender, phone, password);

        if (validationMessage != null) {
            model.addAttribute("mess", validationMessage);
            return "auth/register";
        }

        userService.sendOtpMail(session, email, firstName, lastName, address, gender, phone, password);

        return "redirect:/otp-check";
    }


}
