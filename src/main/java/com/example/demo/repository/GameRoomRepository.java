package com.example.demo.repository;

import com.example.demo.dto.GameRoomDto;
import com.example.demo.entity.GameRoom;
import com.example.demo.entity.User;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.GameRoomStatus;
import com.example.demo.entity.enums.GameUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface GameRoomRepository extends JpaRepository<GameRoom, UUID> {
    //@Query("SELECT gr FROM game_rooms gr where gr.status = ?1 AND gr.id in (select ugr.gameRoom.id from users_profile_game_rooms ugr where ugr.status = ?2 and ugr.id not in (select ugr2.id from users_profile_game_rooms ugr2 where ugr2.userProfile = ?3)) ORDER BY gr.changeDate ASC LIMIT 1")
    @Query("SELECT gr FROM game_rooms gr where gr.status = ?1 AND gr.id in (select ugr.gameRoom.id from users_profile_game_rooms ugr where ugr.status = ?2 and ugr.userProfile.id not in (select ugr2.userProfile.id from users_profile_game_rooms ugr2 where ugr2.userProfile != ?3 and ugr2.gameRoom.id in (select ugr3.gameRoom.id from users_profile_game_rooms ugr3 where ugr3.userProfile = ?3))) ORDER BY gr.changeDate ASC LIMIT 1")
    Optional<GameRoom> findFirstOldRoomByStatus(GameRoomStatus status, GameUserStatus gameUserStatus, UserProfile userProfile);

    @Query("SELECT gr FROM game_rooms gr where gr.status = ?1 AND gr.id in (select ugr.gameRoom.id from users_profile_game_rooms ugr where ugr.userProfile = ?2)")
    Optional<GameRoom> findByStatusAndUserProfile(GameRoomStatus status, UserProfile userProfile);
}
