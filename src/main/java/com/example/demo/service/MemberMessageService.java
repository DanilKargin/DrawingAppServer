package com.example.demo.service;

import com.example.demo.controller.domain.request.group.MemberMessageRequest;
import com.example.demo.dto.MemberMessageDto;
import com.example.demo.entity.MemberMessage;
import com.example.demo.entity.User;
import com.example.demo.repository.MemberMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberMessageService {
    private final MemberMessageRepository memberMessageRepository;
    private final UserProfileService userProfileService;
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    public List<MemberMessageDto> getMessagesHistory(User user){
        var group = groupService.findGroupByUser(user);
        return memberMessageRepository.findByGroupOrderBySendDateAsc(group).stream().map(MemberMessageDto::new).collect(Collectors.toList());
    }

    public MemberMessageDto sendMessage(MemberMessageRequest request){
        try {
            var groupMember = groupMemberService.findGroupMemberByUserProfile(userProfileService.findById(request.getUserProfileId()));
            var message = MemberMessage.builder()
                    .groupMember(groupMember)
                    .sendDate(LocalDateTime.now())
                    .text(request.getText())
                    .build();
            return new MemberMessageDto(memberMessageRepository.save(message));
        }catch(Exception e){
            return null;
        }
    }
}
