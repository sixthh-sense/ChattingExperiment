package com.example.demo.controller;

import com.example.demo.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/api/chat/message") // 웹소켓으로 들어오는 메시지 발행 처리 -> 클라이언트에서는 /pub/api/chat/message로 발행 요청
    public void message(@RequestBody ChatMessageDto message, @Header("token") String token) {

        if (ChatMessageDto.MessageType.ENTER.equals(message.getType()))
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);

    }
}
