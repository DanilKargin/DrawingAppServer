package com.example.demo.dto;

import com.example.demo.entity.GameRoom;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GameRoomDto {
    private String id;
    private byte[] image;
    private int health;
    private String term;
    private int termLength;
    private String description;
    private String letters;

    public GameRoomDto(GameRoom room){
        this.id = room.getId().toString();
        this.image = room.getImage();
        this.health = room.getHealth();
        if(room.getWord() != null) {
            this.term = room.getWord().getTerm();
            this.description = room.getWord().getDescription();
            this.termLength = room.getWord().getTerm().length();
        }
    }
}
