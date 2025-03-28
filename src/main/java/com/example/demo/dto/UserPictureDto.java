package com.example.demo.dto;

import com.example.demo.entity.UserPicture;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPictureDto {
    private String id;
    private byte[] image;
    private LocalDateTime createDate;
    private int likes;
    private String userProfileId;

    public UserPictureDto(UserPicture userPicture){
        this.id = userPicture.getId().toString();
        this.image = userPicture.getImage();
        this.createDate = userPicture.getCreateDate();
        this.likes = userPicture.getLikes();
        this.userProfileId = userPicture.getUserProfile().getId().toString();
    }
}
