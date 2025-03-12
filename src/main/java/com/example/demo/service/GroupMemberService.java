package com.example.demo.service;

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
        return groupMemberRepository.findByUserProfileAndMemberRoleNotIn(userProfile, List.of(MemberRole.NOT_CONFIRMED, MemberRole.EXCLUDED))
                .orElseThrow(() -> new NotFoundException("Участник не найден."));
    }
    public boolean existsByUserProfileAndMemberRoleIsNotIn(UserProfile userProfile, Collection<MemberRole> memberRole){
        return groupMemberRepository.existsByUserProfileAndMemberRoleNotIn(userProfile, memberRole);
    }
    public Optional<GroupMember> findByUserProfileAndGroup(UserProfile userProfile, Group group){
        return groupMemberRepository.findByUserProfileAndGroup(userProfile, group);
    }
    public void delete(GroupMember groupMember){
        groupMemberRepository.delete(groupMember);
    }
}
