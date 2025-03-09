package com.example.demo.repository;

import com.example.demo.entity.GroupLogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupLogoRepository extends JpaRepository<GroupLogo, UUID> {
}
