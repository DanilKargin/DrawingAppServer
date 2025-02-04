package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByDeviceId(String deviceId);
    boolean existsByEmail(String email);
}
