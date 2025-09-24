package com.swp.carcare.controller.employee;

import com.swp.carcare.entity.AppointmentEntity;
import com.swp.carcare.entity.EmployeeEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.AppointmentRepository;
import com.swp.carcare.repository.EmployeeRepository;
import com.swp.carcare.repository.OwnerRepository;
import com.swp.carcare.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeHomeController {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String employee(Model model, HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(username).orElse(null);
        EmployeeEntity employee = employeeRepository.findByUser(userEntity);
        List<AppointmentEntity> appointments = appointmentRepository.findByEmployee(employee);
        model.addAttribute("appointments", appointments);
        model.addAttribute("employee", employee);
        model.addAttribute("appointmentCount", appointmentRepository.countByEmployee(employee));
        model.addAttribute("ownerCount", appointmentRepository.countDistinctOwnersByEmployee(employee));
        return "employee/dashboard";
    }
}