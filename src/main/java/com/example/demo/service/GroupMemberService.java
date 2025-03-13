package com.example.demo.service;

import com.example.demo.dto.GroupMemberDto;
import com.example.demo.entity.Group;
import com.example.demo.entity.GroupMember;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.MemberRole;
import com.example.demo.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;

    public GroupMember save(GroupMember groupMember){
        return groupMemberRepository.save(groupMember);
    }
    public GroupMember findGroupMemberByUserProfile(UserProfile userProfile){
        return groupMemberRepository.findByUserProfileAndMemberRoleIn(userProfile, List.of(MemberRole.MEMBER, MemberRole.OFFICER, MemberRole.LEADER))
                .orElseThrow(() -> new NotFoundException("Участник не найден."));
    }
    public boolean existsByUserProfileAndMemberRoleIsIn(UserProfile userProfile, Collection<MemberRole> memberRole){
        return groupMemberRepository.existsByUserProfileAndMemberRoleIn(userProfile, memberRole);
    }
    public Optional<GroupMember> findByUserProfileAndGroup(UserProfile userProfile, Group group){
        return groupMemberRepository.findByUserProfileAndGroup(userProfile, group);
    }
    public List<GroupMember> findGroupMembersByGroupAndMemberRoles(Group group, Collection<MemberRole> memberRoles){
        return groupMemberRepository.findAllByGroupAndMemberRoleIn(group, memberRoles);
    }
    public void delete(GroupMember groupMember){
        groupMemberRepository.delete(groupMember);
    }
}
