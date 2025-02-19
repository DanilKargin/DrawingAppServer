package com.example.demo.controller.domain.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String email;
    private String password;
    private String token;
}
