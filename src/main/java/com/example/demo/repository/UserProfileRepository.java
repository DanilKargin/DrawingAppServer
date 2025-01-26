package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUser(User user);
}
