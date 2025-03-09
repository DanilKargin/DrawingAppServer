package com.example.demo.dto;

import com.example.demo.entity.GroupLogo;
import lombok.Data;

@Data
public class GroupLogoDto {
    private String id;
    private byte[] image;

    public GroupLogoDto(GroupLogo groupLogo){
        this.id = groupLogo.getId().toString();
        this.image = groupLogo.getImage();
    }
}
