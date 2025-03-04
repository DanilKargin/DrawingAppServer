package com.example.demo.controller;

import com.example.demo.dto.WordDto;
import com.example.demo.service.WordService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.demo.config.SecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequestMapping("/api/word")
@RequiredArgsConstructor
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class WordController {
    private final WordService wordService;

    @GetMapping("/get-random-list")
    public ResponseEntity<List<WordDto>> getRandomList(){
        return ResponseEntity.ok(wordService.getRandomWords());
    }
}
