package com.example.demo.dto;

import com.example.demo.entity.UserGameRoom;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserGameRoomDto {
    private String id;
    private String roomId;
    private String userNickname = "";
    private LocalDateTime changeDate;

    public UserGameRoomDto(UserGameRoom userGameRoom){
        this.id = userGameRoom.getId().toString();
        this.roomId = userGameRoom.getGameRoom().getId().toString();
        if(userGameRoom.getUserProfile() != null) {
            this.userNickname = userGameRoom.getUserProfile().getNickname();
        }
        this.changeDate = userGameRoom.getGameRoom().getChangeDate();
    }
}
