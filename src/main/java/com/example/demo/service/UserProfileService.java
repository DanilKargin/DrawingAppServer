package com.example.demo.service;

import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import com.example.demo.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userRepository;

    public UserProfile save(UserProfile user) {
        return userRepository.save(user);
    }


    public UserProfile create(User user) {
        var checkUserProfile = userRepository.findByUser(user);
        if(checkUserProfile.isPresent()){
            return null;
        }
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        var userProfile = UserProfile.builder()
                .currency(1000)
                .energy(10)
                .nickname("Игрок-" + String.format("%06d", number))
                .user(user)
                .build();
        return save(userProfile);
    }
    public UserProfileDto findByUser(User user){
        return new UserProfileDto(userRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("Данные пользователя не найдены")));
    }
}
