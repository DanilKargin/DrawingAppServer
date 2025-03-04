package com.example.demo.entity;

import com.example.demo.entity.enums.GameUserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="users_profile_game_rooms")
public class UserGameRoom {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="user_profile_id")
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name="game_room_id", nullable = false)
    private GameRoom gameRoom;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameUserStatus status;
}
