package com.swp.carcare.repository;

import com.swp.carcare.entity.UserEntity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SpringBootApplication
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public UserEntity save(UserEntity user);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmailAndPassword(String email, String password);
    int countAllByStatus(Integer status);
}
