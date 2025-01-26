package com.example.demo.controller;

import com.example.demo.controller.domain.request.SignInRequest;
import com.example.demo.controller.domain.request.SignUpRequest;
import com.example.demo.controller.domain.request.VerifyRequest;
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
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authenticationService.signUp(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<MessageResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }
    @PutMapping("/verify")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestBody VerifyRequest request) {
        return ResponseEntity.ok(userService.verifyEmail(request));
    }
    @PutMapping("/resend-token")
    public ResponseEntity<MessageResponse> resendToken(@RequestBody VerifyRequest request) {
        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isPresent() && userOpt.get().getRole() == UserRole.NOT_CONFIRMED) {
            User user = userOpt.get();
            return ResponseEntity.ok(new MessageResponse(authenticationService.regenerateToken(user), ""));
        } else {
            return ResponseEntity.ok(new MessageResponse("", "Пользователь не найден или уже подтвержден."));
        }
    }
}
