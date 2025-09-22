package com.swp.carcare.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.EmployeeRepository;
import com.swp.carcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("")
    public String userListPage(Model model,
                               @RequestParam(value = "email", required = false) String emailParam,
                               @RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "5") int size) {

        List<UserEntity> allUsers = userRepository.findAll();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(username).orElse(null);

        if (emailParam != null && !emailParam.isEmpty()) {
            allUsers = allUsers.stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().contains(emailParam.toLowerCase()))
                    .collect(Collectors.toList());
        }

        int totalItems = allUsers.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int start = Math.min(page * size, totalItems);
        int end = Math.min(start + size, totalItems);

        List<UserEntity> users = allUsers.subList(start, end);

        model.addAttribute("users", users);
        model.addAttribute("email", emailParam);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentUserEmail", username);

        return "admin/employee/list";
    }

    // Form thêm mới
    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "admin/employee/add";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("userDto") @Valid UserDto userDto,
                           BindingResult result,
                           Model model) {

        if (result.hasErrors()) {
            return "admin/employee/add";
        }

        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("emailError", "Email đã tồn tại rồi");
            return "admin/employee/add";
        }


        UserEntity user = new UserEntity();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(selectedRole);
        user.setStatus(userDto.getStatus());

        UserEntity save = userRepository.save(user);

        return "redirect:/admin/employee?add=true";
    }


    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable("id") Long id, Model model) {
        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setStatus(user.getStatus());
            dto.setRoleName(user.getRole().getName().name());

            model.addAttribute("userDto", dto);
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/employee/edit";
        }
        return "redirect:/admin/employee";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("userDto") @Valid UserDto userDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "admin/employee/edit";
        }

        Optional<UserEntity> emailUser = userRepository.findByEmail(userDto.getEmail());
        if (emailUser.isPresent() && !emailUser.get().getId().equals(id)) {
            model.addAttribute("emailError", "Email is already registered.");
            return "admin/employee/edit";
        }

        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            user.setEmail(userDto.getEmail());
            user.setStatus(userDto.getStatus());

            if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            userRepository.save(user);

        }

        return "redirect:/admin/employee?edit=true";
    }


    @PostMapping("/update-status/{id}")
    public String updateUserStatus(@PathVariable Long id,
                                   @RequestParam("status") Integer status) {
        Optional<UserEntity> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            UserEntity user = optional.get();
            user.setStatus(status);
            userRepository.save(user);

        }
        return "redirect:/admin/employee?update=true";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        Optional<UserEntity> optional = userRepository.findById(id);
        String email = optional.map(UserEntity::getEmail).orElse("");
        userRepository.deleteById(id);

        return "redirect:/admin/employee?delete=true";
    }
}
