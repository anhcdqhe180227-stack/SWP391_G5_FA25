package com.swp.carcare.controller;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.List;


@Controller
public class HomeController {

    @GetMapping("/")
    private String indexHome(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (authorities.stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/admin/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_EMP".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/employee/dashboard";
        } else if (authorities.stream().anyMatch(authority -> "ROLE_RECEP".equals(authority.getAuthority()))) {
            model.addAttribute("email", username);
            return "redirect:/recep/dashboard";
        } else {
            return "index";
        }
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        return indexHome(model);
    }

    @GetMapping("/index")
    public String index(Model model) {
        return indexHome(model);
    }


}