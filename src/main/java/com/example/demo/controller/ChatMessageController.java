package com.example.demo.controller;

import com.example.demo.dto.ChatMessageRequestDto;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(@RequestBody ChatMessageRequestDto messageRequestDto) {
        // , @Header("token") String token
        User sender =  userService.getUser(messageRequestDto.getSenderId());
        ChatMessage chatMessage = new ChatMessage(messageRequestDto, sender);
        chatMessageService.sendChatMessage(chatMessage);
        System.out.println("메세지 송부 요청 완료");
    }

}
