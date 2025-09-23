package com.swp.carcare.repository;

import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.UserEntity;
import com.swp.carcare.entity.VehicleEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@SpringBootApplication
public interface VehicleRepository extends JpaRepository<VehicleEntity, Integer> {

    List<VehicleEntity> findByOwner(OwnerEntity ownerEntity);
}
