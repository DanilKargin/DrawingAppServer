package com.example.demo.entity;

import com.example.demo.entity.enums.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="group_members")
public class GroupMember {
    @Id
    @GeneratedValue
    @Column(name="id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name="member_role", nullable = false)
    private MemberRole memberRole;

    @ManyToOne
    @JoinColumn(name="user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name="group_id", nullable = false)
    private Group group;
}
