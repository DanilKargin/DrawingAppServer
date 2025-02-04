package com.example.demo.controller;

import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.service.UserProfileService;
import com.example.demo.service.UserService;
import com.example.demo.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserProfileService userProfileService;
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> GetProfileData(){
        return ResponseEntity.ok(userService.getUserProfile());
    }
}
