package com.example.demo.service;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    @Transactional // 메세지의 type 을 확인하고 그에따라 작업을 분기시킴
    public void sendChatMessage(ChatMessage message) {
        // 자료형을 ChatMessage.class라고 controller에서 지정해줘서 입력변수 자료형이 ChatMessage로 바뀜

        User user = userRepository.findById(message.getSenderId()).orElseThrow(
                ()-> new IllegalArgumentException("(채팅방) 유저인덱스를 찾을 수 없습니다")
        );

        if (ChatMessage.MessageType.TALK.equals(message.getType())) {
            System.out.println("채팅 TALK 들어옴");
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        }
    }

}
