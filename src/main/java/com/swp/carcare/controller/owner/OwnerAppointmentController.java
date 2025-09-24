package com.swp.carcare.controller.owner;


import com.swp.carcare.entity.AppointmentEntity;
import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.AppointmentRepository;
import com.swp.carcare.repository.OwnerRepository;
import com.swp.carcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/owner/appointments")
public class OwnerAppointmentController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;


    @GetMapping("")
    public String form(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            return "redirect:/login";
        }
        OwnerEntity ownerEntity = ownerRepository.findByUser(user).get();

        if (ownerEntity != null) {
            model.addAttribute("owner", ownerEntity);
            return "owner/appointments";
        }
        return "redirect:/";
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
        if(user == null) {
            return "redirect:/login";
        }
        OwnerEntity ownerEntity = ownerRepository.findByUser(user).get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate appointmentDate = LocalDate.parse(date, formatter);

        boolean alreadyHasAppointment = appointmentRepository.existsByOwnerAndAppointmentDateTimeBetween(
                ownerEntity,
                appointmentDate.atStartOfDay(),
                appointmentDate.plusDays(1).atStartOfDay()
        );

        if (alreadyHasAppointment) {
            return "redirect:/owner/appointments?duplicate=true";
        }

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setOwner(ownerEntity);
        appointment.setName(name);
        appointment.setEmail(email);
        appointment.setEmployee(null);
        appointment.setPhoneNumber(phone);
        appointment.setAppointmentDateTime(appointmentDate.atStartOfDay());
        appointment.setStatus(-1);
        appointment.setReason(reason);

        appointmentRepository.save(appointment);

        return "redirect:/owner/appointments?success=true";
    }

}
