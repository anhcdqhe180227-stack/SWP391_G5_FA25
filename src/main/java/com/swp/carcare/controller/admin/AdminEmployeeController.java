package com.swp.carcare.controller.admin;

import java.util.*;
import java.util.stream.Collectors;

import com.swp.carcare.dto.EmployeeDto;
import com.swp.carcare.entity.EmployeeEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.EmployeeRepository;
import com.swp.carcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/employee")
public class AdminEmployeeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public String getAllEmployees(
            Model model) {
        List<EmployeeDto> employees = getAllEmployees();
        model.addAttribute("employees", employees);
        return "admin/employee/view";
    }

    public List<EmployeeDto> getAllEmployees() {
        List<EmployeeEntity> employeeEntities = employeeRepository.findAll();

        List<EmployeeDto> employeeDtos = new ArrayList<>();
        for (EmployeeEntity employee : employeeEntities) {
            EmployeeDto dto = new EmployeeDto(
                    employee.getId(),
                    employee.getFullName(),
                    employee.getGender(),
                    employee.getPhoneNumber(),
                    employee.getUser().getEmail(),
                    employee.getStatus()
            );
            employeeDtos.add(dto);
        }

        return employeeDtos;
    }


    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("employeeDto", new EmployeeDto());
        return "admin/employee/add";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("employeeDto") @Valid EmployeeDto employeeDto,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "admin/employee/add";
        }

        if (userRepository.findByEmail(employeeDto.getEmail()).isPresent()) {
            model.addAttribute("emailError", "Email đã tồn tại rồi!");
            return "admin/employee/add";
        }

        UserEntity user = new UserEntity();
        user.setEmail(employeeDto.getEmail());
        user.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        user.setStatus(employeeDto.getStatus());
        user.setRole(2);

        UserEntity savedUser = userRepository.save(user);

        EmployeeEntity employee = new EmployeeEntity();
        employee.setFullName(employeeDto.getFullName());
        employee.setGender(employeeDto.getGender());
        employee.setDescription(employeeDto.getDescription());
        employee.setPhoneNumber(employeeDto.getPhoneNumber());
        employee.setUser(savedUser);

        employeeRepository.save(employee);

        return "redirect:/admin/employee?add=true";
    }


    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Integer id, Model model) {
        Optional<EmployeeEntity> optional = employeeRepository.findById(id);

        if (optional.isPresent()) {
            EmployeeEntity employeeEntity = optional.get();

            EmployeeDto dto = new EmployeeDto();
            dto.setId(employeeEntity.getId());
            dto.setEmail(employeeEntity.getUser().getEmail());
            dto.setStatus(employeeEntity.getStatus());
            dto.setFullName(employeeEntity.getFullName());
            dto.setGender(employeeEntity.getGender());
            dto.setDescription(employeeEntity.getDescription());
            dto.setPhoneNumber(employeeEntity.getPhoneNumber());

            model.addAttribute("employeeDto", dto);
            return "admin/employee/edit";
        }

        return "redirect:/admin/employee";
    }


    @PostMapping("/update")
    public String updateUser(@RequestParam("id") Integer id,
                             @ModelAttribute("employeeDto") @Valid EmployeeDto employeeDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "admin/employee/edit";
        }

        Optional<EmployeeEntity> optionalEmp = employeeRepository.findById(id);
        if (optionalEmp.isPresent()) {
            EmployeeEntity employee = optionalEmp.get();
            employee.setFullName(employeeDto.getFullName());
            employee.setGender(employeeDto.getGender());
            employee.setDescription(employeeDto.getDescription());
            employee.setPhoneNumber(employeeDto.getPhoneNumber());
            employeeRepository.save(employee);
            return "redirect:/admin/employee?update=true";
        }
        return "redirect:/admin/employee";
    }

    @GetMapping("/update-status/{id}/{newStatus}")
    public String toggleEmployeeStatus(@PathVariable("id") Integer id,
                                       @PathVariable("newStatus") Integer newStatus) {
        Optional<EmployeeEntity> employeeOpt = employeeRepository.findById(id);
        EmployeeEntity employee = employeeOpt.get();
        employee.setStatus(newStatus);
        employeeRepository.save(employee);
        return "redirect:/admin/employee?updateStatus=true";
    }
}
