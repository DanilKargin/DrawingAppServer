package com.example.demo.handler;

import com.example.demo.controller.domain.request.group.MemberMessageRequest;
import com.example.demo.service.MemberMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class GroupChatWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> groupSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final MemberMessageService memberMessageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String groupId = getGroupId(session);
        groupSessions.computeIfAbsent(groupId, k -> new CopyOnWriteArrayList<>()).add(session);
        System.out.println("Пользователь подключился к группе с ID: " + groupId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String groupId = getGroupId(session);
        MemberMessageRequest request = objectMapper.readValue(message.getPayload(), MemberMessageRequest.class);
        var memberMessageDto = memberMessageService.sendMessage(request);
        String responseJson = objectMapper.writeValueAsString(memberMessageDto);
        for (WebSocketSession s : groupSessions.getOrDefault(groupId, new CopyOnWriteArrayList<>())) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(responseJson));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String groupId = getGroupId(session);
        groupSessions.getOrDefault(groupId, new CopyOnWriteArrayList<>()).remove(session);
        System.out.println("Пользователь отключился из группы с ID: " + groupId);
    }

    private String getGroupId(WebSocketSession session) {
        return session.getUri().getQuery().replace("groupId=", "");
    }
}
