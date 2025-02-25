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
    Optional<UserGameRoom> findByUserProfileIsNotAndGameRoom(UserProfile userProfile, GameRoom gameRoom);
    @Query("SELECT ugr FROM UserGameRoom ugr WHERE ugr.room in (SELECT ugr2.room FROM UserGameRoom ugr2 WHERE ugr2.userProfile = ?1 and ugr.status != ?2) and ugr.userProfile != ?1 ORDER BY ugr.room.changeDate DESC")
    List<UserGameRoom> findAllByUserProfileIsNotAndStatusIsNot(UserProfile userProfile, GameUserStatus status);
    @Query("SELECT ugr FROM UserGameRoom ugr WHERE ugr.room in (SELECT ugr2.room FROM UserGameRoom ugr2 WHERE ugr2.userProfile = ?1 and ugr.status = ?2) and ugr.userProfile != ?1 ORDER BY ugr.room.changeDate DESC")
    List<UserGameRoom> findAllByUserProfileIsNotAndStatus(UserProfile userProfile, GameUserStatus status);
}
