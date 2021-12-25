package com.example.demo.pubsub;

import com.example.demo.model.ChatMessage;
import com.example.demo.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    // 클라이언트에서 메세지가 도착하면 해당 메세지를 messagingTemplate 으로 컨버팅하고 다른 구독자들에게 전송한뒤 해당 메세지를 DB에 저장함
    public void sendMessage(String publishMessage) {
        try {
            // ChatMessage 객체로 mapping
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);

            // 채팅방 구독하는 client쪽에 메세지 발송
            messagingTemplate.convertAndSend("/sub/api/chat/rooms/" + chatMessage.getRoomId(), chatMessage);

            // 이 부분이 밀착과 북클럽이 다른 부분. 메세지 저장 여부(북클럽에선 여기서 저장 X)
            ChatMessage message = new ChatMessage();
            message.setType(chatMessage.getType());
            message.setRoomId(chatMessage.getRoomId());
            message.setSender(chatMessage.getSender());
            message.setMessage(chatMessage.getMessage());
            chatMessageRepository.save(message);

        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
