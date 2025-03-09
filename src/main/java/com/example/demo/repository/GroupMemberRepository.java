package com.example.demo.repository;

import com.example.demo.entity.Group;
import com.example.demo.entity.GroupMember;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    boolean existsByUserProfileAndMemberRoleIsNot(UserProfile userProfile, MemberRole memberRole);
    List<GroupMember> findAllByGroup(Group group);
    Optional<GroupMember> findByUserProfileAndGroup(UserProfile userProfile, Group group);
    Optional<GroupMember> findByUserProfileAndMemberRoleIsNot(UserProfile userProfile, MemberRole memberRole);
}
