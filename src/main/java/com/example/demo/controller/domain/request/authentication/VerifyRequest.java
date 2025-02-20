package com.example.demo.controller.domain.request.authentication;

import lombok.Data;

@Data
public class VerifyRequest {
    private String email;
    private String token;
}
