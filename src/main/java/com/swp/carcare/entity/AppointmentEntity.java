package com.swp.carcare.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "Appointments")
@Data
public class AppointmentEntity extends AbstractEntity {

    @Column(name = "appointment_date_time")
    protected LocalDateTime appointmentDateTime;

    @Column(name = "reason")
    private String reason;

    private String name;

    private String phoneNumber;

    private String email;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private OwnerEntity owner;

    @ManyToOne
    @JoinColumn(name = "empId")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private EmployeeEntity employee;

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OwnerEntity getOwner() {
        return owner;
    }

    public void setOwner(OwnerEntity owner) {
        this.owner = owner;
    }

    public EmployeeEntity getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeEntity employee) {
        this.employee = employee;
    }
}
