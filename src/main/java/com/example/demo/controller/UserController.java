package com.example.demo.controller;

import com.example.demo.controller.domain.request.user.UserPictureRequest;
import com.example.demo.controller.domain.request.user.UserProfileRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.UserPictureDto;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.User;
import com.example.demo.service.UserPictureService;
import com.example.demo.service.UserProfileService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/picture/list")
    public ResponseEntity<List<UserPictureDto>> getUserPictures(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(userPictureService.getUserPictures(user));
    }
    @GetMapping("/picture/record-list")
    public ResponseEntity<List<UserPictureDto>> getRecordUserPictures(){
        return ResponseEntity.ok(userPictureService.getUserPicturesOrderByLike());
    }
    @GetMapping("/{id}/picture/list")
    public ResponseEntity<List<UserPictureDto>> getUserPicturesByUserProfileId(@PathVariable("id") String id){
        return ResponseEntity.ok(userPictureService.getUserPicturesByUserProfileId(id));
    }
    @PostMapping("/picture/create")
    public ResponseEntity<MessageResponse> createPicture(@AuthenticationPrincipal User user, @RequestBody UserPictureRequest request){
        return ResponseEntity.ok(userPictureService.createPicture(user, request));
    }
    @DeleteMapping("/picture")
    public ResponseEntity<MessageResponse> deletePictureById(@AuthenticationPrincipal User user, @RequestBody UserPictureRequest request){
        return ResponseEntity.ok(userPictureService.deletePicture(user, request));
    }
}
