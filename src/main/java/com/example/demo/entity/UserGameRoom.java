package com.example.demo.entity;

import com.example.demo.entity.enums.GameUserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="users_profile_game_rooms")
public class UserGameRoom {
    @ManyToOne
    @JoinColumn(name="user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name="game_room_id", nullable = false)
    private GameRoom room;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameUserStatus status;
}
