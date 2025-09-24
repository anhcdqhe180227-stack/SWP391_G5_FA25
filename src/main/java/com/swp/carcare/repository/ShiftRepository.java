package com.swp.carcare.repository;

import com.swp.carcare.entity.EmployeeEntity;
import com.swp.carcare.entity.ShiftEntity;
import com.swp.carcare.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@SpringBootApplication
public interface ShiftRepository extends JpaRepository<ShiftEntity, Integer> {
    Page<ShiftEntity> findByEmployeeId(Integer id, Pageable pageable);

    List<ShiftEntity> findByEmployeeId(Integer id);

    Page<ShiftEntity> findByStartTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<ShiftEntity> findByEmployeeIdAndStartTimeBetween(Integer empId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT s FROM ShiftEntity s WHERE " +
            "(:empId IS NULL OR s.employee.id = :empId) AND " +
            "(s.startTime < :endTime AND s.endTime > :startTime) AND " +
            "(:currentId IS NULL OR s.id <> :currentId)")
    List<ShiftEntity> findConflictingShifts(@Param("empId") Integer empId,
                                            @Param("startTime") LocalDateTime startTime,
                                            @Param("endTime") LocalDateTime endTime,
                                            @Param("currentId") Integer currentId);

}
