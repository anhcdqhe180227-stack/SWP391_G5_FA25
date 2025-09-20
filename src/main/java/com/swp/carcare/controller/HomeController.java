package com.swp.carcare.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    private String indexHome(Model model) {
       return "index";
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