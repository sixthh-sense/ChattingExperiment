package com.example.demo.service;

import com.example.demo.dto.ChatRoomDto;
import com.example.demo.model.ChatRoom;
import com.example.demo.model.User;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    //private Map<String, ChatRoom> chatRoomMap;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장


    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;


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

    public ChatRoom createChatRoom(ChatRoomDto chatRoomDto) {
        ChatRoom chatRoom=chatRoomRepository.findByRoomId(chatRoomDto.getRoomId());
        if(chatRoom==null){
            chatRoom = ChatRoom.create(chatRoomDto);

            for(String email : chatRoomDto.getChatUser()){
                User tempUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
                System.out.println(tempUser);
                chatRoom.getUser().add(tempUser);
                System.out.println(chatRoom.getUser());
            }
            hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
            chatRoomRepository.save(chatRoom);
        }

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