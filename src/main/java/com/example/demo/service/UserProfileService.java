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

    @Value("${gameConstants.energy.valueMax}")
    private int ENERGY_MAX;
    @Value("${gameConstants.energy.recoveryTime}")
    private long ENERGY_RECOVERY_TIME;

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
                .energy(ENERGY_MAX)
                .nickname("Игрок-" + String.format("%06d", number))
                .user(user)
                .build();
        return save(userProfile);
    }
    public UserProfile findByUser(User user){
        var userProfileOpt = userProfileRepository.findByUser(user);
        if(userProfileOpt.isPresent()){
            var userProfile = userProfileOpt.get();
            synchronizeEnergy(userProfile);
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
    @Transactional
    public boolean subtractEnergy(UserProfile user){
        var userProfileOpt = userProfileRepository.findById(user.getId());
        if(userProfileOpt.isPresent()){
            var userProfile = userProfileOpt.get();
            synchronizeEnergy(userProfile);
            if(userProfile.getEnergy() <= 0){
                return false;
            }else if(userProfile.getEnergy() >= ENERGY_MAX){
                userProfile.setEnergy(ENERGY_MAX - 1);
                userProfile.setEnergyTime(LocalDateTime.now().plusMinutes(ENERGY_RECOVERY_TIME));
            }else{
                userProfile.setEnergy(userProfile.getEnergy() - 1);
                userProfile.setEnergyTime(userProfile.getEnergyTime().plusMinutes(ENERGY_RECOVERY_TIME));
            }
            save(userProfile);
            return true;
        }
        return false;
    }

    public void deleteUserProfile(User user){
        var userProfile = userProfileRepository.findByUser(user);
        if(userProfile.isPresent()) {
            userProfileRepository.delete(userProfile.get());
        }
    }
    private void synchronizeEnergy(UserProfile userProfile){
        if(userProfile.getEnergyTime() != null && userProfile.getEnergy() < ENERGY_MAX){
            if(userProfile.getEnergyTime().isBefore(LocalDateTime.now())){
                userProfile.setEnergy(ENERGY_MAX);
                userProfile.setEnergyTime(null);
            }else{
                double minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), userProfile.getEnergyTime());
                int delta = (int)Math.floor(minutes / ENERGY_RECOVERY_TIME);
                int energy = userProfile.getEnergy() + delta;
                userProfile.setEnergy(energy < ENERGY_MAX ? energy : ENERGY_MAX);
                userProfile.setEnergyTime(userProfile.getEnergyTime().minusMinutes(delta * ENERGY_RECOVERY_TIME));
            }
        }
    }
}
