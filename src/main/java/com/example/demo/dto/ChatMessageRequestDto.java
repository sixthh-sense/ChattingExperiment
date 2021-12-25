package com.example.demo.dto;

import com.example.demo.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequestDto {
    private ChatMessage.MessageType type;
    private String roomId;
    private Long senderId;
    private String message;
}
