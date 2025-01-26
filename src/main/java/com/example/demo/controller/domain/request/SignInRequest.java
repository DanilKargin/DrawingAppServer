package com.example.demo.controller.domain.request;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
}
