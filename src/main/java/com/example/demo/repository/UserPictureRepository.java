package com.example.demo.repository;

import com.example.demo.entity.UserPicture;
import com.example.demo.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPictureRepository extends JpaRepository<UserPicture, UUID> {
    List<UserPicture> findAllByUserProfile(UserProfile userProfile);
    List<UserPicture> findAllByCreateDateAfterOrderByLikesDesc(LocalDateTime createDate);
    Optional<UserPicture> findByUserProfileAndId(UserProfile userProfile, UUID id);
}
