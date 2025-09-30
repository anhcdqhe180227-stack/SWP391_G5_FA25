package com.swp.carcare.controller.owner;

import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.UserRepository;
import com.swp.carcare.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/owner/appointments")
public class OwnerAppointmentController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppointmentService ownerAppointmentService;

    @GetMapping("")
    public String form(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "owner/appointments";
    }

    @PostMapping("/save")
    public String save(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String date,
            @RequestParam String reason
    ) {

        String emailUser = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(emailUser).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        boolean alreadyHasAppointment = ownerAppointmentService.isDuplicateAppointment(user, date);
        if (alreadyHasAppointment) {
            return "redirect:/owner/appointments?duplicate=true";
        }

        ownerAppointmentService.createAppointment(user, name, phone, email, date, reason);

        return "redirect:/owner/appointments?success=true";
    }
}
