package com.example.demo.service;

import com.example.demo.controller.domain.response.MessageResponse;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import com.example.demo.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    @Value("${gameConstants.userPicture.deltaStorageSize}")
    private int DELTA_STORAGE_SIZE;
    @Value("${gameConstants.userPicture.deltaStoragePrice}")
    private int DELTA_STORAGE_PRICE;

    public UserProfile save(UserProfile user) {
        return userProfileRepository.save(user);
    }


    public UserProfile create(User user) {
        var checkUserProfile = userProfileRepository.findByUser(user);
        if(checkUserProfile.isPresent()){
            return null;
        }
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        var userProfile = UserProfile.builder()
                .currency(1000)
                .pictureMaxCount(9)
                .nickname("Игрок-" + String.format("%06d", number))
                .user(user)
                .build();
        return save(userProfile);
    }
    public UserProfile findByUser(User user){
        var userProfileOpt = userProfileRepository.findByUser(user);
        if(userProfileOpt.isPresent()){
            var userProfile = userProfileOpt.get();
            return save(userProfile);
        }
        return null;
    }
    public UserProfile findById(String id){
        return userProfileRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    public MessageResponse changeNickname(String nickname, User user){
        if(!nickname.isEmpty()){
            var userProfileOpt = userProfileRepository.findByUser(user);
            if(userProfileOpt.isPresent()){
                var userProfile = userProfileOpt.get();
                userProfile.setNickname(nickname);
                save(userProfile);
                return new MessageResponse("Имя пользователя успешно изменено.", "");
            }else{
                return new MessageResponse("", "Профиля не существует!");
            }
        }else{
            return new MessageResponse("", "Никнейм не может быть пустым.");
        }
    }

    public MessageResponse buyPictureStorageSize(User user, int multiplier){
        var userProfileOpt = userProfileRepository.findByUser(user);
        if(userProfileOpt.isPresent()){
            var userProfile = userProfileOpt.get();
            if(userProfile.getCurrency() >= DELTA_STORAGE_PRICE * multiplier){
                userProfile.setCurrency(userProfile.getCurrency() - DELTA_STORAGE_PRICE * multiplier);
                userProfile.setPictureMaxCount(userProfile.getPictureMaxCount() + DELTA_STORAGE_SIZE * multiplier);
                save(userProfile);
                return new MessageResponse("Количество мест увеличено.", "");
            }else{
                return new MessageResponse("", "Недостаточно валюты!");
            }
        }else{
            return new MessageResponse("", "Профиля не существует!");
        }
    }

    public void deleteUserProfile(User user){
        var userProfile = userProfileRepository.findByUser(user);
        if(userProfile.isPresent()) {
            userProfileRepository.delete(userProfile.get());
        }
    }
}
