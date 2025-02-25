package com.example.demo.controller.domain.request.gameRoom;

import lombok.Data;

@Data
public class GameRoomRequest {
    private String roomId;
    private byte[] image;
    private String word;
}
