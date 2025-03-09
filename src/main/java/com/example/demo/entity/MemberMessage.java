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
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="member_messages")
public class MemberMessage {
    @Id
    @GeneratedValue
    @Column(name="id")
    private UUID id;

    @Column(nullable = false)
    private String text;

    @Column(name="send_date", nullable = false)
    private LocalDateTime sendDate;

    @ManyToOne
    @JoinColumn(name="group_member_id", nullable = false)
    private GroupMember groupMember;
}
