package com.example.demo.controller;

import com.example.demo.dto.ChatRoomCreateResponseDto;
import com.example.demo.dto.ChatRoomResponseDto;
import com.example.demo.model.ChatRoom;
import com.example.demo.model.User;
import com.example.demo.oauth2.UserDetailsImpl;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    public List<ChatRoomCreateResponseDto> room() {
        return chatRoomService.findAllRoom();
    }

    // 채팅방 생성
    @PostMapping("/room")
    public ChatRoomCreateResponseDto createRoom(@RequestParam String name, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(() -> new IllegalArgumentException("유저가 없습니다"));
        return chatRoomService.createChatRoom(name, user);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ChatRoomResponseDto roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomById(roomId);
    }

    // 사용자별 채팅방 목록 조회
}
