package com.swp.carcare.controller.admin;

import com.swp.carcare.dto.EmployeeDto;
import com.swp.carcare.entity.EmployeeEntity;
import com.swp.carcare.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/employee")
public class AdminEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String getAllEmployees(Model model) {
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "admin/employee/view";
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

        boolean isSaved = employeeService.saveEmployee(employeeDto);
        if (!isSaved) {
            model.addAttribute("emailError", "Email đã tồn tại rồi!");
            return "admin/employee/add";
        }

        return "redirect:/admin/employee?add=true";
    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Integer id, Model model) {
        EmployeeEntity employeeEntity = employeeService.getEmployeeById(id).orElse(null);

        if (employeeEntity != null) {
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

        boolean isUpdated = employeeService.updateEmployee(id, employeeDto);
        if (isUpdated) {
            return "redirect:/admin/employee?update=true";
        }

        return "redirect:/admin/employee";
    }

    @GetMapping("/update-status/{id}/{newStatus}")
    public String toggleEmployeeStatus(@PathVariable("id") Integer id,
                                       @PathVariable("newStatus") Integer newStatus) {
        boolean isUpdated = employeeService.toggleEmployeeStatus(id, newStatus);
        if (isUpdated) {
            return "redirect:/admin/employee?updateStatus=true";
        }
        return "redirect:/admin/employee";
    }
}
