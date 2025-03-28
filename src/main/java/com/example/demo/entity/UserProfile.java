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
@Entity(name = "users_profile")
public class UserProfile {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private int currency;

    @Column(name="picture_max_count", nullable = false)
    private int pictureMaxCount;

    @Column(nullable = false)
    private int energy;

    @Column(name="energy_time")
    private LocalDateTime energyTime;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
