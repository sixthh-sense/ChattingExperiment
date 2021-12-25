package com.example.demo.service;

import com.example.demo.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final SimpMessageSendingOperations messagingTemplate;

    // 메세지 type을 확인한 뒤 그에 따라 작업 분기
//    public void sendChatMessage(ChatMessage chatMessage) {
//        //채팅방 입장
//        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
//            chatMessage.setMessage("MBTI가 " + chatMessage.getSender().getMbti() + "인 " + chatMessage.getSender().getNickname() + "님이 입장하셨습니다.");
//            chatMessage.setSender(chatMessage.getSender());
//            // 채팅방 퇴장시
//        } else if (ChatMessage.MessageType.LEAVE.equals(chatMessage.getType())) {
//            chatMessage.setMessage(chatMessage.getSender().getUsername() + "님이 나가셨습니다.");
//            chatMessage.setSender(chatMessage.getSender());
//        }
//        messagingTemplate.convertAndSend("/sub/api/chat/room/" + chatMessage.getRoomId(), chatMessage);
//    }

//    public String getRoomId(String orElse) {
//    }
}
