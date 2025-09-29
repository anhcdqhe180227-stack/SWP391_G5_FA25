package com.swp.carcare.controller.recep;

import com.swp.carcare.dto.OwnerDto;
import com.swp.carcare.dto.VehicleDto;
import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.VehicleEntity;
import com.swp.carcare.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recep/owners")
public class RecepOwnerController {

    @Autowired
    private OwnerRepository ownerRepository;

    @GetMapping
    public String getAllOwners(Model model) {
        List<OwnerEntity> owners = ownerRepository.findAll();
        List<OwnerDto> ownerDtos = owners.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        model.addAttribute("owners", ownerDtos);
        return "recep/owners/view";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Optional<OwnerEntity> optional = ownerRepository.findById(id);

        if (optional.isPresent()) {
            OwnerEntity ownerEntity = optional.get();
            OwnerDto ownerDto = convertToDto(ownerEntity);
            model.addAttribute("owner", ownerDto);
            return "recep/owners/detail";
        }

        return "redirect:/recep/owners";
    }

    public OwnerDto convertToDto(OwnerEntity ownerEntity) {
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setId(ownerEntity.getId());
        ownerDto.setUserId(ownerEntity.getUser().getId());
        ownerDto.setEmail(ownerEntity.getUser().getEmail());
        ownerDto.setAvatar(ownerEntity.getUser().getAvatar());
        ownerDto.setFirstName(ownerEntity.getFirstName());
        ownerDto.setLastName(ownerEntity.getLastName());
        ownerDto.setGender(ownerEntity.getGender());
        ownerDto.setAddress(ownerEntity.getAddress());
        ownerDto.setPhoneNumber(ownerEntity.getPhoneNumber());
        ownerDto.setStatus(ownerEntity.getUser().getStatus());

        Set<VehicleDto> vehicleDtos = new HashSet<>();
        for (VehicleEntity vehicle : ownerEntity.getVehicles()) {
            vehicleDtos.add(convertToDto(vehicle));
        }
        ownerDto.setVehicles(vehicleDtos);

        return ownerDto;
    }

    public VehicleDto convertToDto(VehicleEntity vehicleEntity) {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(vehicleEntity.getId());
        vehicleDto.setOwnerId(vehicleEntity.getOwner().getId());
        vehicleDto.setModel(vehicleEntity.getModel());
        vehicleDto.setLicensePlate(vehicleEntity.getLicensePlate());
        vehicleDto.setYear(vehicleEntity.getYear());
        vehicleDto.setImg(vehicleEntity.getImg());
        return vehicleDto;
    }

}
