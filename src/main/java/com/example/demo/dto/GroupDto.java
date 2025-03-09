package com.example.demo.dto;

import com.example.demo.entity.Group;
import com.example.demo.entity.GroupMember;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class GroupDto {
    private String id;
    private String name;
    private String tag;
    private String description;
    private String logoId;
    private String type;
    private List<GroupMemberDto> members;

    public GroupDto(Group group){
        this.id = group.getId().toString();
        this.name = group.getName();
        this.tag = group.getTag();
        this.description = group.getDescription();
        this.logoId = group.getGroupLogo().getId().toString();
        this.type = group.getType().toString();
        this.members = group.getMembers().stream().map(GroupMemberDto::new).collect(Collectors.toList());
    }
}
