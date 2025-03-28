package com.example.demo.dto;

import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import lombok.Data;

@Data
public class UserProfileDto {
    private String id;
    private String nickname;
    private int currency;
    private int energy;
    private int pictureMaxCount;
    private String email;

    public UserProfileDto(UserProfile user){
        this.id = user.getId().toString();
        this.nickname = user.getNickname();
        this.currency = user.getCurrency();
        this.energy = user.getEnergy();
        this.pictureMaxCount = user.getPictureMaxCount();
        this.email = user.getUser().getEmail();
    }
}
