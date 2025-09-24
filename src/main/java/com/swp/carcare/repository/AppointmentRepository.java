package com.swp.carcare.repository;

import com.swp.carcare.entity.AppointmentEntity;
import com.swp.carcare.entity.EmployeeEntity;
import com.swp.carcare.entity.OwnerEntity;
import com.swp.carcare.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@SpringBootApplication
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Integer> {
    boolean existsByOwnerAndAppointmentDateTimeBetween(OwnerEntity owner, LocalDateTime start, LocalDateTime end);

    List<AppointmentEntity> findByEmployee(EmployeeEntity employee);

    long countByEmployee(EmployeeEntity employee);

    @Query("SELECT COUNT(DISTINCT a.owner) FROM AppointmentEntity a WHERE a.employee = :employee")
    long countDistinctOwnersByEmployee(@Param("employee") EmployeeEntity employee);
}
