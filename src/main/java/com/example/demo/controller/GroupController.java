package com.example.demo.controller;

import com.example.demo.controller.domain.request.group.GroupRequest;
import com.example.demo.controller.domain.request.group.SearchGroupRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.GroupDto;
import com.example.demo.dto.GroupLogoDto;
import com.example.demo.dto.MemberMessageDto;
import com.example.demo.entity.GroupLogo;
import com.example.demo.entity.User;
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
    @GetMapping("/chat/history")
    public ResponseEntity<List<MemberMessageDto>> getChatHistory(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(memberMessageService.getMessagesHistory(user));
    }
    @PostMapping("/create")
    public ResponseEntity<GroupDto> createGroup(@AuthenticationPrincipal User user, @RequestBody GroupRequest request){
        return ResponseEntity.ok(groupService.createGroup(user, request));
    }
    /*@PostMapping("/create-logo")
    public ResponseEntity<String> createLogo(@RequestBody GroupLogo logo){
        groupService.createLogo(logo);
        return ResponseEntity.ok("Все гуд!");
    }*/
    @PostMapping("/join")
    public ResponseEntity<MessageResponse> joinGroup(@AuthenticationPrincipal User user, @RequestBody SearchGroupRequest request){
        return ResponseEntity.ok(groupService.joinGroup(user, request));
    }
    @DeleteMapping("/quit")
    public ResponseEntity<MessageResponse> quitFromGroup(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(groupService.quitFromGroup(user));
    }
}
