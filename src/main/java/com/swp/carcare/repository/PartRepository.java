package com.swp.carcare.repository;

import com.swp.carcare.entity.PartEntity;
import com.swp.carcare.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SpringBootApplication
public interface PartRepository extends JpaRepository<PartEntity, Integer> {

}
