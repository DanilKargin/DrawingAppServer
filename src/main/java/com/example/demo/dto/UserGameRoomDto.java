package com.example.demo.dto;

import com.example.demo.entity.UserGameRoom;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserGameRoomDto {
    private String roomId;
    private String userNickname;
    private LocalDateTime changeDate;

    public UserGameRoomDto(UserGameRoom userGameRoom){
        this.roomId = userGameRoom.getRoom().getId().toString();
        this.userNickname = userGameRoom.getUserProfile().getNickname();
        this.changeDate = userGameRoom.getRoom().getChangeDate();
    }
}
