package com.swp.carcare.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "Parts") // Table name is now Parts
@Data
public class PartEntity extends AbstractEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "part_status")
    @Enumerated(EnumType.STRING)
    private PartStatus partStatus;

    private String img;

    @ManyToOne
    @JoinColumn(name = "vehicle_id") // Corrected column name
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private VehicleEntity vehicle;

    public enum PartStatus {
        OUT_OF_DATE,
        BROKEN,
        EXCELLENT
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public VehicleEntity getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleEntity vehicle) {
        this.vehicle = vehicle;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public PartStatus getPartStatus() {
        return partStatus;
    }

    public void setPartStatus(PartStatus partStatus) {
        this.partStatus = partStatus;
    }
}
