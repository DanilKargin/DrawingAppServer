package com.example.demo.controller;

import com.example.demo.controller.domain.request.user.UserProfileRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.service.UserProfileService;
import com.example.demo.service.UserService;
import com.example.demo.service.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.example.demo.config.SecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class UserController {
    private final UserService userService;
    private final UserProfileService userProfileService;

    @GetMapping("/get-profile")
    public ResponseEntity<UserProfileDto> GetProfileData(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(userProfileService.findByUser(user));
    }

    @PutMapping("/change-nickname")
    public ResponseEntity<MessageResponse> ChangeNickname(@AuthenticationPrincipal User user, @RequestBody UserProfileRequest request){
        return ResponseEntity.ok(userProfileService.changeNickname(request.getNickname(), user));
    }
}
