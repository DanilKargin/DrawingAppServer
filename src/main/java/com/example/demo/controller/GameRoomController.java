package com.example.demo.controller;

import com.example.demo.controller.domain.request.gameRoom.GameRoomRequest;
import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.GameRoomDto;
import com.example.demo.dto.UserGameRoomDto;
import com.example.demo.entity.User;
import com.example.demo.service.GameRoomService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.SecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequestMapping("/api/game-room")
@RequiredArgsConstructor
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class GameRoomController {
    private final GameRoomService gameRoomService;

    @GetMapping
    public ResponseEntity<GameRoomDto> findGameRoomById(@AuthenticationPrincipal User user, @RequestParam String id){
        return ResponseEntity.ok(gameRoomService.getGameRoomById(user, id));
    }
    @GetMapping("/ready-list")
    public ResponseEntity<List<UserGameRoomDto>> getReadyList(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(gameRoomService.getReadyRoomList(user));
    }
    @GetMapping("/waiting-list")
    public ResponseEntity<List<UserGameRoomDto>> getWaitingList(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(gameRoomService.getWaitingRoomList(user));
    }
    @GetMapping("/find-free")
    public ResponseEntity<GameRoomDto> findFreeGameRoom(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(gameRoomService.findGameRoom(user));
    }
    @PutMapping("/set-image")
    public ResponseEntity<MessageResponse> setImageInRoom(@AuthenticationPrincipal User user, @RequestBody GameRoomRequest request) {
        return ResponseEntity.ok(gameRoomService.setImageInRoom(user, request));
    }
    @PutMapping("/set-word")
    public ResponseEntity<GameRoomDto> setWordInRoom(@AuthenticationPrincipal User user, @RequestBody GameRoomRequest request){
        return ResponseEntity.ok(gameRoomService.setWordInRoom(user, request));
    }
    @PutMapping("/check-word-input")
    public ResponseEntity<GameRoomDto> checkWordInput(@AuthenticationPrincipal User user, @RequestBody GameRoomRequest request){
        return ResponseEntity.ok(gameRoomService.checkWord(user, request));
    }
}
