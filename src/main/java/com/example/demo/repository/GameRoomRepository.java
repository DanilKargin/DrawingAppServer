package com.example.demo.repository;

import com.example.demo.entity.GameRoom;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.GameRoomStatus;
import com.example.demo.entity.enums.GameUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface GameRoomRepository extends JpaRepository<GameRoom, UUID> {
    @Query("SELECT gr FROM game_rooms gr where gr.status = ?1 AND gr.id in (select ugr.gameRoom.id from users_profile_game_rooms ugr where ugr.status = ?2 and ugr.userProfile != ?3) ORDER BY gr.changeDate ASC LIMIT 1")
    Optional<GameRoom> findFirstOldRoomByStatus(GameRoomStatus status, GameUserStatus status2, UserProfile userProfile);
}
