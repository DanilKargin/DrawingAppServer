package com.example.demo.dto;

import com.example.demo.entity.MemberMessage;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberMessageDto {
    private String id;
    private String memberNickname;
    private String userProfileId;
    private String text;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendDate;
    private String memberRole;
    private String type;

    public MemberMessageDto(MemberMessage memberMessage){
        this.id = memberMessage.getId().toString();
        this.text = memberMessage.getText();
        this.userProfileId = memberMessage.getGroupMember().getUserProfile().getId().toString();
        this.memberNickname = memberMessage.getGroupMember().getUserProfile().getNickname();
        this.sendDate = memberMessage.getSendDate();
        this.memberRole = memberMessage.getGroupMember().getMemberRole().toString();
        this.type = memberMessage.getType().toString();
    }
}

