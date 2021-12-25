package com.example.demo.service;

import com.example.demo.model.ChatRoom;
import com.example.demo.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    //private Map<String, ChatRoom> chatRoomMap;
    private final ChatRoomRepository chatRoomRepository;

//    @PostConstruct
//    private void init() {
//        chatRoomMap = new LinkedHashMap<>();
//    }

    public List<ChatRoom> findAllRoom() {
        // 채팅방 생성순서 최근 순으로 반환
        List<ChatRoom> chatRooms;
        chatRooms = chatRoomRepository.findAll();
        Collections.reverse(chatRooms);
        return chatRooms;
    }

    public ChatRoom findRoomById(String id) {
        return chatRoomRepository.findByRoomId(id);
    }

    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .name(name)
                .build();
        chatRoomRepository.save(chatRoom);
        //chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

//    public void setUserEnterInfo(String sessionId, String roomId, Long userId) {
//    }
//
//    public String getUserEnterRoomId(String sessionId) {
//    }
//
//    public void removeUserEnterInfo(String sessionId) {
//    }
//
//    public User chkSessionUser(String sessionId) {
//    }
}