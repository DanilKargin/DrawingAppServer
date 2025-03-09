package com.example.demo.controller;

import com.example.demo.controller.domain.request.user.UserPictureRequest;
import com.example.demo.controller.domain.request.user.UserProfileRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.UserPictureDto;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.service.UserPictureService;
import com.example.demo.service.UserProfileService;
import com.example.demo.service.UserService;
import com.example.demo.service.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.example.demo.config.SecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class UserController {
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final UserPictureService userPictureService;

    @GetMapping("/get-profile")
    public ResponseEntity<UserProfileDto> getProfileData(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(new UserProfileDto(userProfileService.findByUser(user)));
    }

    @PutMapping("/change-nickname")
    public ResponseEntity<MessageResponse> changeNickname(@AuthenticationPrincipal User user, @RequestBody UserProfileRequest request){
        return ResponseEntity.ok(userProfileService.changeNickname(request.getNickname(), user));
    }
    @GetMapping("/pictures")
    public ResponseEntity<List<UserPictureDto>> getUserPictures(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(userPictureService.getUserPictures(user));
    }
    @GetMapping("/pictures/id")
    public ResponseEntity<List<UserPictureDto>> getUserPicturesById(@RequestParam String id){
        return ResponseEntity.ok(userPictureService.getUserPicturesById(id));
    }
    @PostMapping("/picture/create")
    public ResponseEntity<MessageResponse> createPicture(@AuthenticationPrincipal User user, @RequestBody UserPictureRequest request){
        return ResponseEntity.ok(userPictureService.createPicture(user, request));
    }
    @DeleteMapping("/picture")
    public ResponseEntity<MessageResponse> deletePictureById(@AuthenticationPrincipal User user, @RequestParam String id){
        return ResponseEntity.ok(userPictureService.deletePicture(user, id));
    }
}
