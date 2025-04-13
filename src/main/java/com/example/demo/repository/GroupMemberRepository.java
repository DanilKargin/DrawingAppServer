package com.example.demo.repository;

import com.example.demo.entity.Group;
import com.example.demo.entity.GroupMember;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    boolean existsByUserProfileAndMemberRoleIn(UserProfile userProfile, Collection<MemberRole> memberRoles);
    List<GroupMember> findAllByGroup(Group group);
    Optional<GroupMember> findByUserProfileAndGroup(UserProfile userProfile, Group group);
    Optional<GroupMember> findByIdAndGroupAndMemberRoleIn(UUID id, Group group, Collection<MemberRole> memberRoles);
    Optional<GroupMember> findByUserProfileAndMemberRoleIn(UserProfile userProfile, Collection<MemberRole> memberRoles);
    List<GroupMember> findAllByGroupAndMemberRoleIn(Group group, Collection<MemberRole> memberRoles);
    @Query("SELECT gm FROM group_members gm where gm.memberRole = 'LEADER' and gm.group.id = ?1")
    Optional<GroupMember> findLeaderByGroupId(UUID groupId);
}
