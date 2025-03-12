package com.example.demo.controller.domain.request.group;

import lombok.Data;

@Data
public class MemberMessageRequest {
    private String text;
    private String userProfileId;
}
