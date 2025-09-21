package com.swp.carcare.controller.recep;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recep")
public class RecepHomeController {
    @GetMapping("/dashboard")
    public String home(Model model, HttpSession session) {
        return "recep/dashboard";
    }
}