package com.example.demo.service;

import com.example.demo.controller.domain.request.group.GroupRequest;
import com.example.demo.controller.domain.request.group.SearchGroupRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.GroupDto;
import com.example.demo.dto.GroupLogoDto;
import com.example.demo.dto.GroupMemberDto;
import com.example.demo.entity.*;
import com.example.demo.entity.enums.GroupType;
import com.example.demo.entity.enums.MemberRole;
import com.example.demo.repository.GroupLogoRepository;
import com.example.demo.repository.GroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserProfileService userProfileService;
    private final GroupMemberService groupMemberService;
    private final GroupLogoRepository groupLogoRepository;

    @Value("${gameConstants.group.createPrice}")
    private int CREATE_PRICE;

    @Value("${gameConstants.group.maxMemberCount}")
    private int MAX_MEMBER_COUNT;

    public List<GroupDto> getGroupList(){
        return groupRepository.findAll().stream().map(GroupDto::new).collect(Collectors.toList());
    }
    public List<GroupMemberDto> getGroupMembers(String groupId){
        try{
            var group = findGroupById(UUID.fromString(groupId));
            return groupMemberService.findGroupMembersByGroupAndMemberRoles(group, List.of(MemberRole.MEMBER, MemberRole.OFFICER, MemberRole.LEADER)).stream().map(GroupMemberDto::new).collect(Collectors.toList());
        }catch(Exception e){
            return null;
        }
    }
    public List<GroupLogoDto> getGroupLogoList(){
        return groupLogoRepository.findAll().stream().map(GroupLogoDto::new).collect(Collectors.toList());
    }
    public GroupDto getCurrentUserGroup(User user){
        var group = findGroupByUser(user);
        if(group != null){
            return new GroupDto(group);
        }else{
            return new GroupDto();
        }
    }
    public void createLogo(GroupLogo logo){
        var groupLogo = GroupLogo.builder()
                .image(logo.getImage())
                .build();
        groupLogoRepository.save(groupLogo);
    }
    @Transactional
    public GroupDto createGroup(User user, GroupRequest request){
        var userProfile = userProfileService.findByUser(user);
        var logo = groupLogoRepository.findById(UUID.fromString(request.getLogoId()));
        if(userProfile != null && logo.isPresent()){
            int checkCurrency = userProfile.getCurrency() - CREATE_PRICE;
            if(checkCurrency >= 0){
                if(!groupMemberService.existsByUserProfileAndMemberRoleIsIn(userProfile, List.of(MemberRole.MEMBER, MemberRole.OFFICER, MemberRole.LEADER))) {
                    userProfile.setCurrency(checkCurrency);
                    var group = Group.builder()
                            .name(request.getName())
                            .tag("#"+RandomStringUtils.random(8, true, true).toUpperCase())
                            .description(request.getDescription())
                            .groupLogo(logo.get())
                            .type(GroupType.valueOf(request.getType()))
                            .build();
                    var result = groupRepository.save(group);
                    var leaderMember = GroupMember.builder()
                            .memberRole(MemberRole.LEADER)
                            .group(result)
                            .userProfile(userProfileService.save(userProfile))
                            .build();
                    var leader = groupMemberService.save(leaderMember);
                    result.getMembers().add(leader);
                    return new GroupDto(result);
                }
            }else{
                return new GroupDto();
            }
        }
        return null;
    }

    public MessageResponse joinGroup(User user, SearchGroupRequest request){
        try {
            var userProfile = userProfileService.findByUser(user);
            if(!groupMemberService.existsByUserProfileAndMemberRoleIsIn(userProfile, List.of(MemberRole.MEMBER, MemberRole.OFFICER, MemberRole.LEADER))) {
                var group = findGroupById(UUID.fromString(request.getId()));
                var groupMemberOpt = groupMemberService.findByUserProfileAndGroup(userProfile, group);
                if (groupMemberOpt.isEmpty()) {
                    if (group.getType() == GroupType.FREE_ENTRY) {
                        var groupMember = GroupMember.builder()
                                .group(group)
                                .memberRole(MemberRole.MEMBER)
                                .userProfile(userProfile)
                                .build();
                        groupMemberService.save(groupMember);
                        return new MessageResponse("Вы успешно вступили в " + group.getName(), "");
                    } else if (group.getType() == GroupType.ENTRY_ON_REQUEST) {
                        var groupMember = GroupMember.builder()
                                .group(group)
                                .memberRole(MemberRole.NOT_CONFIRMED)
                                .userProfile(userProfile)
                                .build();
                        groupMemberService.save(groupMember);
                        return new MessageResponse("Успешная заявка.", "");
                    } else {
                        return new MessageResponse("Вступление только по приглашениям.", "");
                    }
                }else{
                    var groupMember = groupMemberOpt.get();
                    if (group.getType() == GroupType.FREE_ENTRY) {
                        groupMember.setMemberRole(MemberRole.MEMBER);
                        groupMemberService.save(groupMember);
                        return new MessageResponse("Вы успешно вступили в " + group.getName(), "");
                    } else if (group.getType() == GroupType.ENTRY_ON_REQUEST) {
                        groupMember.setMemberRole(MemberRole.NOT_CONFIRMED);
                        groupMemberService.save(groupMember);
                        return new MessageResponse("Успешная заявка.", "");
                    } else {
                        return new MessageResponse("Вступление только по приглашениям.", "");
                    }
                }
            }
            return new MessageResponse("", "Пользователь уже состоит в группе.");
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }

    public MessageResponse quitFromGroup(User user){
        try {
            var userProfile = userProfileService.findByUser(user);
            var groupMember = groupMemberService.findGroupMemberByUserProfile(userProfile);
                if (groupMember.getMemberRole() == MemberRole.LEADER) {
                    var group = findGroupById(groupMember.getGroup().getId());

                }
                groupMember.setMemberRole(MemberRole.EXCLUDED);
                groupMemberService.save(groupMember);
                return new MessageResponse("Вы успешно покинули " + groupMember.getGroup().getName(), "");
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }

    public Group findGroupByUser(User user){
        try {
            var userProfile = userProfileService.findByUser(user);
            var groupMember = groupMemberService.findGroupMemberByUserProfile(userProfile);
            return groupMember.getGroup();
        }catch (Exception e){
            return null;
        }
    }
    private Group findGroupById(UUID id){
        return groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Группа не найдена."));
    }
}
