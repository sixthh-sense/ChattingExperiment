package com.example.demo.service;

import com.example.demo.dto.ChatRoomCreateResponseDto;
import com.example.demo.dto.ChatRoomResponseDto;
import com.example.demo.model.ChatRoom;
import com.example.demo.model.User;
import com.example.demo.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {


    private final ChatRoomRepository chatRoomRepository;

    //redis -> 여기서 사용하는 자료형이 HashOperations. <Key, HashKey, HashValue>
    // Redis CacheKeys
    private static final String CHAT_ROOM = "CHAT_ROOM"; // 채팅룸 저장
    public static final String ENTER_INFO = "ENTER_INFO"; //채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    public static final String USER_INFO = "USER_INFO"; //채팅방에 입장한 클라이언트 수 저장

    private final RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsUserInfo;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    //채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;

    private void init(){
        hashOpsChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    // 모든 채팅방 목록 return
    @Transactional
    public List<ChatRoomCreateResponseDto> findAllRoom() {

        List<ChatRoom> chatRooms;
        chatRooms = chatRoomRepository.findAll();

        List<ChatRoomCreateResponseDto> allList = new ArrayList<>();
        for (ChatRoom room : chatRooms ) {
            ChatRoomCreateResponseDto responseDto = ChatRoomCreateResponseDto.builder()
                    .roomId(room.getRoomId())
                    .name(room.getName())
                    .ownUserId(room.getOwnUserId())
                    .build();
            allList.add(responseDto);
        }
        // 채팅방 생성순서 최근 순으로 반환
        Collections.reverse(allList);
        return allList;
    }

//    private ChatRoomCreateResponseDto createDto(ChatRoom room) {
//        return new ChatRoomCreateResponseDto(
//                room.getRoomId(),
//                room.getName(),
//                room.getOwnUserId()
//        );
//    }

    //방 하나 조회
    @Transactional
    public ChatRoomResponseDto findRoomById(String room) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(room);
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getName())
                .ownUserId(chatRoom.getOwnUserId())
                .build();
    }

    // 채팅방 생성
    @Transactional // 이건 붙여야 하나 말아야 하나?
    public ChatRoomCreateResponseDto createChatRoom(String name, User user) {

        ChatRoom chatRoom = ChatRoom.create(name, user);
        chatRoomRepository.save(chatRoom);

        return ChatRoomCreateResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .name(name)
                .ownUserId(user.getId())
                .build();
    }

    public ChannelTopic getTopic(String roomId){
        return topics.get(roomId);
    }

    // redis 에 입장정보로 sessionId 와 roomId를 저장하고 해당 sessionId 와 토큰에서 받아온 userId를 저장
    public void setUserEnterInfo(String sessionId, String roomId, Long userId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
        hashOpsUserInfo.put(USER_INFO, sessionId, Long.toString(userId));
    }

    // redis 에 저장했던 sessionId 로 roomId를 리턴
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }
}