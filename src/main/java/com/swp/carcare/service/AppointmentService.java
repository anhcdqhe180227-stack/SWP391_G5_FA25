package com.swp.carcare.service;

import com.swp.carcare.entity.AppointmentEntity;
import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.repository.AppointmentRepository;
import com.swp.carcare.repository.OwnerRepository;
import com.swp.carcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class AppointmentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public boolean isDuplicateAppointment(UserEntity user, String date) {
        OwnerEntity ownerEntity = ownerRepository.findByUser(user).orElse(null);
        if (ownerEntity == null) {
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate appointmentDate = LocalDate.parse(date, formatter);

        return appointmentRepository.existsByOwnerAndAppointmentDateTimeBetween(
                ownerEntity,
                appointmentDate.atStartOfDay(),
                appointmentDate.plusDays(1).atStartOfDay()
        );
    }

    public AppointmentEntity createAppointment(UserEntity user, String name, String phone, String email, String date, String reason) {
        OwnerEntity ownerEntity = ownerRepository.findByUser(user).get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate appointmentDate = LocalDate.parse(date, formatter);

        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setOwner(ownerEntity);
        appointment.setName(name);
        appointment.setEmail(email);
        appointment.setPhoneNumber(phone);
        appointment.setAppointmentDateTime(appointmentDate.atStartOfDay());
        appointment.setStatus(-1);
        appointment.setReason(reason);

        return appointmentRepository.save(appointment);
    }
}
