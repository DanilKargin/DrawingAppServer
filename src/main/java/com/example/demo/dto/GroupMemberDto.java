package com.example.demo.dto;

import com.example.demo.entity.GroupMember;
import lombok.Data;

@Data
public class GroupMemberDto {
    private String id;
    private String memberRole;
    private String userNickname;

    public GroupMemberDto(GroupMember groupMember){
        this.id = groupMember.getId().toString();
        this.memberRole = groupMember.getMemberRole().toString();
        this.userNickname = groupMember.getUserProfile().getNickname();
    }
}
