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
@Table(name = "Vehicle")
@Data
public class VehicleEntity extends AbstractEntity {

    @Column(name = "model")
    private String model;

    @Column(name = "license_plate")
    private String licensePlate;

    private Integer year;
    private String img;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private OwnerEntity owner;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Set<PartEntity> parts;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public OwnerEntity getOwner() {
        return owner;
    }

    public void setOwner(OwnerEntity owner) {
        this.owner = owner;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Set<PartEntity> getParts() {
        return parts;
    }

    public void setParts(Set<PartEntity> parts) {
        this.parts = parts;
    }
}
