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

import java.util.*;

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
    public List<GroupMember> findGroupMemberRequests(UserProfile userProfile){
        var groupMember = groupMemberRepository.findByUserProfileAndMemberRoleIn(userProfile, List.of(MemberRole.LEADER, MemberRole.OFFICER));
        if(groupMember.isPresent()) {
            return groupMemberRepository.findAllByGroupAndMemberRoleIn(groupMember.get().getGroup(), List.of(MemberRole.NOT_CONFIRMED));
        }else{
            return new ArrayList<>();
        }
    }
    public GroupMember findOfficerGroupMemberByUserProfile(UserProfile userProfile){
        return groupMemberRepository.findByUserProfileAndMemberRoleIn(userProfile, List.of(MemberRole.LEADER, MemberRole.OFFICER))
                .orElseThrow(() -> new NotFoundException("Участник не найден."));
    }
    public GroupMember findByIdAndGroup(UUID id, Group group, Collection<MemberRole> collection){
        return groupMemberRepository.findByIdAndGroupAndMemberRoleIn(id, group, collection)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена."));
    }
    public void delete(GroupMember groupMember){
        groupMemberRepository.delete(groupMember);
    }

    public GroupMember findGroupLeaderByGroupId(UUID groupId) {
        return groupMemberRepository.findLeaderByGroupId(groupId)
                .orElseThrow(()-> new NotFoundException("Участник не найден."));
    }
}
