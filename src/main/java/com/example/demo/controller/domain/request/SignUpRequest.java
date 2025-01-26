package com.example.demo.controller.domain.request;

import lombok.Data;

@Data
public class SignUpRequest {
    private String email;
    private String password;
}
