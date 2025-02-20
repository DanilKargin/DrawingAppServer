package com.example.demo.controller.domain.request.authentication;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
}
