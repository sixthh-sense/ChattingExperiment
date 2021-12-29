package com.example.demo.controller;

import com.example.demo.dto.ChatRoomDto;
import com.example.demo.dto.ResultReturn;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatRoom;
import com.example.demo.model.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.ChatRoomRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatRoomService;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    //기존에 쓰이던 채팅방 리스트 조회 hash 사용
//    @GetMapping("/rooms")
//    @ResponseBody
//    public List<ChatRoom> room() {
//        List<ChatRoom> chatRooms = chatRoomService.findAllRoom();
//        chatRooms.stream().forEach(room -> room.setUserCount(chatRoomService.getUserCount(room.getRoomId())));
//        return chatRooms;
//    }
    //참여중인 채팅방 조회
    @GetMapping("/rooms")
    public ResultReturn profileChange(HttpServletRequest httpServletRequest){
        //토큰에서 사용자 정보 추출
        String token = jwtTokenProvider.resolveToken(httpServletRequest);
        String email = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 조회, 일치하는 E-MAIL이 없습니다"));
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUser(user);
        return new ResultReturn(true, chatRooms,"참여중인 채팅방 조회 완료");
    }


    //채팅방 생성(parameter : roomName, user_email)
    @PostMapping("/create")
    public ResultReturn createRoom(@RequestBody ChatRoomDto chatRoomDto) {

        ChatRoom chatRoom = chatRoomService.createChatRoom(chatRoomDto);

        return new ResultReturn(true, chatRoom,"채팅방 생성 완료");

    }

    //특정 채팅방 입장. 채팅방에 저장된 메세지 반환
    @GetMapping("/enter/{roomId}")
    public ResultReturn roomInfo(@PathVariable String roomId) {
        List<ChatMessage> messages = chatMessageRepository.findAllByRoomIdOrderByTimenowDesc(roomId);
        return new ResultReturn(true, messages,"채팅방 입장, 메세지 조회 완료");
    }

}