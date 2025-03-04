package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user_pictures")
public class UserPicture {
    @Id
    @GeneratedValue
    @Column(name="id")
    private UUID id;

    @Column(nullable = false)
    private byte[] image;

    @Column(name="create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private int likes;

    @ManyToOne
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
}
