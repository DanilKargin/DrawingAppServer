package com.example.demo.controller.domain.request.authentication;

import lombok.Data;

@Data
public class SignUpRequest {
    private String email;
    private String password;
}
