package com.example.demo.repository;

import com.example.demo.entity.MemberMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupMessageRepository extends JpaRepository<MemberMessage, UUID> {
}
