package com.example.demo.controller.domain.request.user;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String nickname;
    private int currency;
}
