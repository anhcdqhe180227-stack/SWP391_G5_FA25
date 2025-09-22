package com.swp.carcare.controller.auth;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/404")
    public String notFound() {
        return "404";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/?logout";
    }
}
