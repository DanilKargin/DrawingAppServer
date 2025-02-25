package com.example.demo.repository;

import com.example.demo.entity.GameRoom;
import com.example.demo.entity.enums.GameRoomStatus;
import com.example.demo.entity.enums.GameUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface GameRoomRepository extends JpaRepository<GameRoom, UUID> {
    @Query("SELECT gr from GameRoom gr JOIN gr.userGameRoomList ugr where gr.status LIKE ?1 AND ugr.status LIKE ?2 ORDER BY gr.changeDate ASC LIMIT 1")
    Optional<GameRoom> findFirstOldRoomByStatus(GameRoomStatus status, GameUserStatus status2);
}
