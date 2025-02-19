package com.example.demo.controller;

import com.example.demo.controller.domain.request.*;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.service.UserService;
import com.example.demo.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<MessageResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }
    @PostMapping("/sign-in-guest")
    public ResponseEntity<MessageResponse> guestSignIn(@RequestBody GuestSignInRequest request){
        return ResponseEntity.ok(authenticationService.guestSignIn(request));
    }
    @PutMapping("/verify")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestBody VerifyRequest request) {
        return ResponseEntity.ok(authenticationService.verifyEmail(request));
    }
    @PutMapping("/send-token")
    public ResponseEntity<MessageResponse> sendToken(@RequestBody VerifyRequest request) {
        return ResponseEntity.ok(authenticationService.sendSecurityToken(request));
    }
    @GetMapping("/check-token")
    public ResponseEntity<MessageResponse> checkToken(@RequestParam String token, @RequestParam String email){
        return ResponseEntity.ok(authenticationService.checkToken(token, email));
    }
    @PutMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody ChangePasswordRequest request){
        return ResponseEntity.ok(authenticationService.changePassword(request));
    }
}
