package com.example.demo.repository;

import com.example.demo.entity.GameRoom;
import com.example.demo.entity.UserGameRoom;
import com.example.demo.entity.UserProfile;
import com.example.demo.entity.enums.GameUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserGameRoomRepository extends JpaRepository<UserGameRoom, UUID> {
    Optional<UserGameRoom> findByUserProfileAndGameRoom(UserProfile userProfile, GameRoom gameRoom);
    @Query("SELECT ugr FROM users_profile_game_rooms ugr WHERE (ugr.userProfile IS NULL OR ugr.userProfile != ?1) and ugr.gameRoom = ?2")
    Optional<UserGameRoom> findFreeRoom(UserProfile userProfile, GameRoom gameRoom);
    Optional<UserGameRoom> findByUserProfileIsNotAndGameRoom(UserProfile userProfile, GameRoom gameRoom);
    @Query("SELECT ugr FROM users_profile_game_rooms ugr WHERE ugr.gameRoom.id in (SELECT ugr2.gameRoom.id FROM users_profile_game_rooms ugr2 WHERE COALESCE(ugr2.userProfile.id, '00000000-0000-0000-0000-000000000000') = ?1 and ugr2.status != ?2) and COALESCE(ugr.userProfile.id, '00000000-0000-0000-0000-000000000000') != ?1 ORDER BY ugr.gameRoom.changeDate DESC")
    List<UserGameRoom> findAllByUserProfileIsNotAndStatusIsNot(UUID userId, GameUserStatus status);
    @Query("SELECT ugr FROM users_profile_game_rooms ugr WHERE ugr.gameRoom.id in (SELECT ugr2.gameRoom.id FROM users_profile_game_rooms ugr2 WHERE COALESCE(ugr2.userProfile.id, '00000000-0000-0000-0000-000000000000') = ?1 and ugr2.status = ?2) and COALESCE(ugr.userProfile.id, '00000000-0000-0000-0000-000000000000') != ?1 ORDER BY ugr.gameRoom.changeDate DESC")
    List<UserGameRoom> findAllByUserProfileIsNotAndStatus(UUID userId, GameUserStatus status);
}
