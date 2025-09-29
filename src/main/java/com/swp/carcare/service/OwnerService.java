package com.swp.carcare.service;

import com.swp.carcare.dto.OwnerDto;
import com.swp.carcare.dto.VehicleDto;
import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.VehicleEntity;
import com.swp.carcare.repository.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    public List<OwnerDto> getAllOwnerDtos() {
        List<OwnerEntity> owners = ownerRepository.findAll();
        return owners.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<OwnerDto> getOwnerDtoById(Integer id) {
        Optional<OwnerEntity> ownerOpt = ownerRepository.findById(id);
        return ownerOpt.map(this::convertToDto);
    }

    private OwnerDto convertToDto(OwnerEntity ownerEntity) {
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

        Set<VehicleDto> vehicleDtos = ownerEntity.getVehicles().stream()
                .map(this::convertVehicleToDto)
                .collect(Collectors.toSet());
        ownerDto.setVehicles(vehicleDtos);

        return ownerDto;
    }

    private VehicleDto convertVehicleToDto(VehicleEntity vehicleEntity) {
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
