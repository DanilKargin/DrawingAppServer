package com.example.demo.controller.domain.request.user;

import lombok.Data;

@Data
public class UserPictureRequest {
    private String id;
    private byte[] image;
}
