package com.example.demo.controller;

import com.example.demo.dto.ChatMessageDto;
import com.example.demo.model.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtDecoder;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final SimpMessageSendingOperations messagingTemplate;

    private final ChatMessageRepository messageRepository;

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final ChatMessageService chatMessageService;
    private final UserService userService;

    @MessageMapping("/api/chat/message") // 웹소켓으로 들어오는 메시지 발행 처리 -> 클라이언트에서는 /pub/api/chat/message로 발행 요청
    public void message(@RequestBody ChatMessage message) {
        // ChatMessageRequestDto requestDto
//        ChatMessage chatMessage = new ChatMessage(requestDto, userService);
//        chatMessageService.sendChatMessage(chatMessage);
        if (ChatMessage.MessageType.ENTER.equals(message.getType()))
            message.setMessage("익명님이 입장하셨습니다.");
        messagingTemplate.convertAndSend("/sub/api/chat/room/" + message.getRoomId(), message);
        messageRepository.save(message);

    }
}
