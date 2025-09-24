package com.swp.carcare.controller.recep;

import com.swp.carcare.entity.AppointmentEntity;
import com.swp.carcare.repository.AppointmentRepository;
import com.swp.carcare.repository.EmployeeRepository;
import com.swp.carcare.repository.OwnerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/recep")
public class RecepHomeController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    @GetMapping("/dashboard")
    public String home(Model model, HttpSession session) {
        List<AppointmentEntity> appointments = appointmentRepository.findAll();
        model.addAttribute("appointments", appointments);
        model.addAttribute("appointmentCount", appointmentRepository.count());
        model.addAttribute("appointmentCount", appointmentRepository.count());
        model.addAttribute("employeeCount", employeeRepository.count());
        model.addAttribute("ownerCount", ownerRepository.count());
        return "recep/dashboard";
    }
}