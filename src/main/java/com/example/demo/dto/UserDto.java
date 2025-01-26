package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.demo.entity.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;

    private String role;

    private String email;

    private String password;

    public UserDto(User user){
        this.id = user.getId().toString();
        this.role = user.getRole().toString();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }
}
