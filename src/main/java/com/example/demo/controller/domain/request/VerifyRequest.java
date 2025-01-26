package com.example.demo.controller.domain.request;

import lombok.Data;

@Data
public class VerifyRequest {
    private String email;
    private String token;
}
