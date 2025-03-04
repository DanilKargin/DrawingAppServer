package com.example.demo.entity;

import com.example.demo.entity.enums.GameRoomStatus;
import com.example.demo.service.GameRoomService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="game_rooms")
public class GameRoom {
    @Id
    @GeneratedValue
    @Column(name="id")
    private UUID id;

    @Column(name="change_date", nullable = false)
    private LocalDateTime changeDate;

    @Column()
    private byte[] image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameRoomStatus status;

    @Column(nullable = false)
    private int health;

    @ManyToOne
    @JoinColumn(name="word_id")
    private Word word;

}
