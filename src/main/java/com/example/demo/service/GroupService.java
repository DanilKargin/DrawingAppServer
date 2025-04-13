package com.example.demo.service;

import com.example.demo.controller.domain.request.group.GroupRequest;
import com.example.demo.controller.domain.request.group.MemberMessageRequest;
import com.example.demo.controller.domain.request.group.SearchGroupRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.GroupDto;
import com.example.demo.dto.GroupLogoDto;
import com.example.demo.dto.GroupMemberDto;
import com.example.demo.entity.*;
import com.example.demo.entity.enums.GroupType;
import com.example.demo.entity.enums.MemberRole;
import com.example.demo.entity.enums.MessageType;
import com.example.demo.handler.GroupChatWebSocketHandler;
import com.example.demo.repository.GroupLogoRepository;
import com.example.demo.repository.GroupRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserProfileService userProfileService;
    private final GroupMemberService groupMemberService;
    private final GroupLogoRepository groupLogoRepository;
    private final GroupChatWebSocketHandler groupChatWebSocketHandler;
    private final ObjectMapper objectMapper;

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
            return groupMemberService.findGroupMembersByGroupAndMemberRoles(group, List.of(MemberRole.MEMBER, MemberRole.OFFICER, MemberRole.LEADER))
                    .stream().map(GroupMemberDto::new).collect(Collectors.toList());
        }catch(Exception e){
            return null;
        }
    }
    public List<GroupMemberDto> getGroupMemberRequests(User user){
        var userProfile = userProfileService.findByUser(user);
        return groupMemberService.findGroupMemberRequests(userProfile)
                .stream().map(GroupMemberDto::new).collect(Collectors.toList());
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
    public Boolean getRequestGroupMemberByGroupId(User user, String groupId){
        try {
            var userProfile = userProfileService.findByUser(user);
            var group = findGroupById(UUID.fromString(groupId));
            var groupMemberOpt = groupMemberService.findByUserProfileAndGroup(userProfile, group);
            if(groupMemberOpt.isPresent() && groupMemberOpt.get().getMemberRole().equals(MemberRole.NOT_CONFIRMED)){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            return null;
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
                    //result.getMembers().add(leader);
                    return new GroupDto(result);
                }
            }else{
                return new GroupDto();
            }
        }
        return null;
    }

    public GroupDto editGroup(User user, GroupRequest request){
        try {
            var userProfile = userProfileService.findByUser(user);
            var logoOpt = groupLogoRepository.findById(UUID.fromString(request.getLogoId()));
            var groupMember = groupMemberService.findGroupMemberByUserProfile(userProfile);
            if (userProfile != null) {
                if(groupMember.getMemberRole().equals(MemberRole.LEADER)){
                    var group = groupMember.getGroup();
                    logoOpt.ifPresent(group::setGroupLogo);
                    group.setName(request.getName());
                    group.setDescription(request.getDescription());
                    group.setType(GroupType.valueOf(request.getType()));
                    return new GroupDto(groupRepository.save(group));
                }
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }
    public MessageResponse joinGroup(User user, SearchGroupRequest request){
        try {
            var userProfile = userProfileService.findByUser(user);
            if(!groupMemberService.existsByUserProfileAndMemberRoleIsIn(userProfile, List.of(MemberRole.MEMBER, MemberRole.OFFICER, MemberRole.LEADER))) {
                var group = findGroupById(UUID.fromString(request.getId()));
                var groupMemberOpt = groupMemberService.findByUserProfileAndGroup(userProfile, group);
                if (groupMemberOpt.isEmpty()) {
                    if (group.getType() == GroupType.FREE_ENTRY) {
                        if(group.getMembers().size() < MAX_MEMBER_COUNT) {
                            var groupMember = GroupMember.builder()
                                    .group(group)
                                    .memberRole(MemberRole.MEMBER)
                                    .userProfile(userProfile)
                                    .build();
                            groupMemberService.save(groupMember);
                            groupChatWebSocketHandler.sendNotificationToGroup(group.getId().toString(), new MemberMessageRequest("Пользователь " + userProfile.getNickname() + " вступил в группу.", "", MessageType.NOTIFICATION.toString()));
                            return new MessageResponse("Вы успешно вступили в " + group.getName(), "");
                        }else{
                            return new MessageResponse("", "В группе нет свободных мест.");
                        }
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
                        groupChatWebSocketHandler.sendNotificationToGroup(group.getId().toString(), new MemberMessageRequest("Пользователь " + userProfile.getNickname() + " вступил в группу.", "", MessageType.NOTIFICATION.toString()));
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

    public MessageResponse cancelRequestGroup(User user, SearchGroupRequest request){
        try{
            var userProfile = userProfileService.findByUser(user);
            var group = findGroupById(UUID.fromString(request.getId()));
            var groupMemberOpt = groupMemberService.findByUserProfileAndGroup(userProfile, group);
            if(groupMemberOpt.isPresent()){
                var groupMember = groupMemberOpt.get();
                if(groupMember.getMemberRole().equals(MemberRole.NOT_CONFIRMED)) {
                    groupMember.setMemberRole(MemberRole.EXCLUDED);
                    groupMemberService.save(groupMember);
                    return new MessageResponse("Заявка отменена.", "");
                }
            }
            return new MessageResponse("", "Заявка не найдена.");
        }catch(Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }

    public MessageResponse acceptRequest(User user, String id){
        try{
            var userProfile = userProfileService.findByUser(user);
            var groupMember = groupMemberService.findOfficerGroupMemberByUserProfile(userProfile);
            var requestGroupMember = groupMemberService.findByIdAndGroup(UUID.fromString(id), groupMember.getGroup(), List.of(MemberRole.NOT_CONFIRMED));
            requestGroupMember.setMemberRole(MemberRole.MEMBER);
            groupMemberService.save(requestGroupMember);
            groupChatWebSocketHandler.sendNotificationToGroup(groupMember.getGroup().getId().toString(), new MemberMessageRequest("Пользователь " + requestGroupMember.getUserProfile().getNickname() + " вступил в группу.", "", MessageType.NOTIFICATION.toString()));
            return new MessageResponse("Пользователь принят.", "");
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    public MessageResponse rejectRequest(User user, String id){
        try{
            var userProfile = userProfileService.findByUser(user);
            var groupMember = groupMemberService.findOfficerGroupMemberByUserProfile(userProfile);
            var requestGroupMember = groupMemberService.findByIdAndGroup(UUID.fromString(id), groupMember.getGroup(), List.of(MemberRole.NOT_CONFIRMED));
            requestGroupMember.setMemberRole(MemberRole.EXCLUDED);
            groupMemberService.save(requestGroupMember);
            return new MessageResponse("Пользователь отклонен.", "");
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    public MessageResponse excludeMember(User user, String id){
        try{
            var userProfile = userProfileService.findByUser(user);
            var groupMember = groupMemberService.findOfficerGroupMemberByUserProfile(userProfile);
            var requestGroupMember = groupMemberService.findByIdAndGroup(UUID.fromString(id), groupMember.getGroup(), List.of(MemberRole.MEMBER));
            requestGroupMember.setMemberRole(MemberRole.EXCLUDED);
            groupMemberService.save(requestGroupMember);
            groupChatWebSocketHandler.sendNotificationToGroup(groupMember.getGroup().getId().toString(), new MemberMessageRequest("Пользователь " + requestGroupMember.getUserProfile().getNickname() + " исключен из группы.", "", MessageType.NOTIFICATION.toString()));
            return new MessageResponse("Пользователь исключен.", "");
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    @Transactional
    public MessageResponse upRoleMember(User user, String id){
        try{
            var userProfile = userProfileService.findByUser(user);
            var groupMember = groupMemberService.findOfficerGroupMemberByUserProfile(userProfile);
            GroupMember requestGroupMember;
            if(groupMember.getMemberRole().equals(MemberRole.LEADER)) {
                requestGroupMember = groupMemberService.findByIdAndGroup(UUID.fromString(id), groupMember.getGroup(), List.of(MemberRole.MEMBER, MemberRole.OFFICER));
            }else{
                requestGroupMember = groupMemberService.findByIdAndGroup(UUID.fromString(id), groupMember.getGroup(), List.of(MemberRole.MEMBER));
            }
            if(requestGroupMember.getMemberRole().equals(MemberRole.MEMBER)){
                requestGroupMember.setMemberRole(MemberRole.OFFICER);
            }else if (requestGroupMember.getMemberRole().equals(MemberRole.OFFICER)){
                requestGroupMember.setMemberRole(MemberRole.LEADER);
                groupMember.setMemberRole(MemberRole.OFFICER);
            }
            groupMemberService.save(requestGroupMember);
            groupMemberService.save(groupMember);
            return new MessageResponse("Пользователь повышен.", "");
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    public MessageResponse lowerRoleMember(User user, String id){
        try{
            var userProfile = userProfileService.findByUser(user);
            var groupMember = groupMemberService.findOfficerGroupMemberByUserProfile(userProfile);
            GroupMember requestGroupMember;
            if(groupMember.getMemberRole().equals(MemberRole.LEADER)) {
                requestGroupMember = groupMemberService.findByIdAndGroup(UUID.fromString(id), groupMember.getGroup(), List.of(MemberRole.OFFICER));
                requestGroupMember.setMemberRole(MemberRole.MEMBER);
                groupMemberService.save(requestGroupMember);
                return new MessageResponse("Пользователь понижен.", "");
            }else{
                return new MessageResponse("", "Нет разрешения.");
            }
        }catch (Exception e){
            return new MessageResponse("", e.getMessage());
        }
    }
    public MessageResponse quitFromGroup(User user){
        try {
            var userProfile = userProfileService.findByUser(user);
            var groupMember = groupMemberService.findGroupMemberByUserProfile(userProfile);
            if (groupMember.getMemberRole() == MemberRole.LEADER) {
                return new MessageResponse("", "Лидер не может покинуть группу.");
            }
            groupMember.setMemberRole(MemberRole.EXCLUDED);
            groupMemberService.save(groupMember);
            groupChatWebSocketHandler.sendNotificationToGroup(groupMember.getGroup().getId().toString(), new MemberMessageRequest("Пользователь " + userProfile.getNickname() + " вышел из группы.", "", MessageType.NOTIFICATION.toString()));
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
