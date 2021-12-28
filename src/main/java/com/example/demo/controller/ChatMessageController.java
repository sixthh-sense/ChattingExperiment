package com.example.demo.controller;

import com.example.demo.model.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final SimpMessageSendingOperations messagingTemplate;

    private final ChatMessageRepository messageRepository;

    @MessageMapping("/chat/message") // 웹소켓으로 들어오는 메시지 발행 처리 -> 클라이언트에서는 /pub/chat/message로 발행 요청
    public void message(@RequestBody ChatMessage message) {
        // ChatMessageRequestDto requestDto
//        ChatMessage chatMessage = new ChatMessage(requestDto, userService);
//        chatMessageService.sendChatMessage(chatMessage);
        if (ChatMessage.MessageType.ENTER.equals(message.getType()))
            message.setMessage("익명님이 입장하셨습니다.");
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        messageRepository.save(message);

    }
}
