package com.swp.carcare.controller.owner;

import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.entity.VehicleEntity;
import com.swp.carcare.repository.OwnerRepository;
import com.swp.carcare.repository.UserRepository;
import com.swp.carcare.repository.VehicleRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/owner/vehicles")
public class OwnerVehicleController {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @GetMapping("")
    public String vehicles(Model model, HttpSession session) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        if(user == null) {
            return "redirect:/login";
        }
        OwnerEntity ownerEntity = ownerRepository.findByUser(user).get();
        List<VehicleEntity> vehicles = vehicleRepository.findByOwner(ownerEntity);
        model.addAttribute("vehicles", vehicles);
        return "owner/vehicles";
    }
}