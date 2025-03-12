package com.example.demo.repository;

import com.example.demo.entity.Group;
import com.example.demo.entity.GroupMember;
import com.example.demo.entity.MemberMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface MemberMessageRepository extends JpaRepository<MemberMessage, UUID> {
    @Query("SELECT m FROM member_messages m WHERE m.groupMember.group = ?1 ORDER BY m.sendDate ASC")
    List<MemberMessage> findByGroupOrderBySendDateAsc(Group group);
}
