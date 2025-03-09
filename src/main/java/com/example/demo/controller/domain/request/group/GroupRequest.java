package com.example.demo.controller.domain.request.group;

import lombok.Data;

@Data
public class GroupRequest {
    private String name;
    private String description;
    private String logoId;
    private String type;
}
