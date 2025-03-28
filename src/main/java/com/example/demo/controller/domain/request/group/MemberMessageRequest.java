package com.example.demo.controller.domain.request.group;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberMessageRequest {
    private String text;
    private String userProfileId;
    private String type;
}
