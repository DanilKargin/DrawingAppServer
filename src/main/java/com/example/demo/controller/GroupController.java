package com.example.demo.controller;

import com.example.demo.controller.domain.request.group.GroupRequest;
import com.example.demo.controller.domain.request.group.SearchGroupRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.GroupDto;
import com.example.demo.dto.GroupLogoDto;
import com.example.demo.dto.GroupMemberDto;
import com.example.demo.dto.MemberMessageDto;
import com.example.demo.entity.*;
import com.example.demo.service.GroupMemberService;
import com.example.demo.service.GroupService;
import com.example.demo.service.MemberMessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.SecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class GroupController {
    private final GroupService groupService;
    private final MemberMessageService memberMessageService;

    @GetMapping("/logos")
    public ResponseEntity<List<GroupLogoDto>> getGroupLogos(){
        return ResponseEntity.ok(groupService.getGroupLogoList());
    }
    @GetMapping
    public ResponseEntity<GroupDto> getUserGroup(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(groupService.getCurrentUserGroup(user));
    }
    @GetMapping("/list")
    public ResponseEntity<List<GroupDto>> getGroupList(){
        return ResponseEntity.ok(groupService.getGroupList());
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(@PathVariable("id") String id){
        return ResponseEntity.ok(groupService.getGroupMembers(id));
    }
    @GetMapping("/{id}/request")
    public ResponseEntity<Boolean> getRequestGroupMember(@AuthenticationPrincipal User user, @PathVariable("id") String id){
        return ResponseEntity.ok(groupService.getRequestGroupMemberByGroupId(user, id));
    }
    @GetMapping("/requests")
    public ResponseEntity<List<GroupMemberDto>> getGroupMemberRequests(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(groupService.getGroupMemberRequests(user));
    }
    @GetMapping("/chat/history")
    public ResponseEntity<List<MemberMessageDto>> getChatHistory(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(memberMessageService.getMessagesHistory(user));
    }
    @PostMapping("/create")
    public ResponseEntity<GroupDto> createGroup(@AuthenticationPrincipal User user, @RequestBody GroupRequest request){
        return ResponseEntity.ok(groupService.createGroup(user, request));
    }
    @PutMapping("/edit")
    public ResponseEntity<GroupDto> editGroup(@AuthenticationPrincipal User user, @RequestBody GroupRequest request){
        return ResponseEntity.ok(groupService.editGroup(user, request));
    }

    @PostMapping("/create-logo")
    public ResponseEntity<String> createLogo(@RequestBody GroupLogo logo){
        groupService.createLogo(logo);
        return ResponseEntity.ok("Все гуд!");
    }
    @PostMapping("/join")
    public ResponseEntity<MessageResponse> joinGroup(@AuthenticationPrincipal User user, @RequestBody SearchGroupRequest request){
        return ResponseEntity.ok(groupService.joinGroup(user, request));
    }
    @PutMapping("/cancel-request")
    public ResponseEntity<MessageResponse> cancelRequestGroup(@AuthenticationPrincipal User user, @RequestBody SearchGroupRequest request){
        return ResponseEntity.ok(groupService.cancelRequestGroup(user, request));
    }
    @PutMapping("/request/{id}/accept")
    public ResponseEntity<MessageResponse> acceptRequest(@AuthenticationPrincipal User user, @PathVariable("id") String id){
        return ResponseEntity.ok(groupService.acceptRequest(user, id));
    }
    @PutMapping("/request/{id}/reject")
    public ResponseEntity<MessageResponse> rejectRequest(@AuthenticationPrincipal User user, @PathVariable("id") String id){
        return ResponseEntity.ok(groupService.rejectRequest(user, id));
    }
    @PutMapping("/member/{id}/exclude")
    public ResponseEntity<MessageResponse> excludeMember(@AuthenticationPrincipal User user, @PathVariable("id") String id){
        return ResponseEntity.ok(groupService.excludeMember(user, id));
    }
    @PutMapping("/member/{id}/up-role")
    public ResponseEntity<MessageResponse> upMemberRole(@AuthenticationPrincipal User user, @PathVariable("id") String id){
        return ResponseEntity.ok(groupService.upRoleMember(user, id));
    }
    @PutMapping("/member/{id}/lower-role")
    public ResponseEntity<MessageResponse> lowerMemberRole(@AuthenticationPrincipal User user, @PathVariable("id") String id){
        return ResponseEntity.ok(groupService.lowerRoleMember(user, id));
    }

    @DeleteMapping("/quit")
    public ResponseEntity<MessageResponse> quitFromGroup(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(groupService.quitFromGroup(user));
    }
}
