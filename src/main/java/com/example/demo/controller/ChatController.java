package com.example.demo.controller;

import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.repository.ChatMessageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatRoomService;
import com.example.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {//ChatService에서 입/퇴장을 처리하기 때문에 간소

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * websocket "/pub/api/chat/message"로 들어오는 메시징을 처리한다.
     */


    @MessageMapping("/api/chat/message") // 웹소켓으로 들어오는 메시지 발행 처리 -> 클라이언트에서는 /pub/api/chat/message로 발행 요청
    public void message(@RequestBody ChatMessage message, @Header("token") String token) {
        String email = jwtTokenProvider.getUserPk(token); //회원의 대화명을 가져와 token 유효성 체크
        User member = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 E-MAIL이 없습니다"));
        String nickname = member.getUsername();
        message.setEmail(email);
        // 헤더에서 토큰을 읽어 로그인 회원 정보로 대화명 설정
        message.setUserName(nickname);
        System.out.println("토큰 유효성 확인 완료, 해당 닉네임 : "+ nickname);
        // 채팅방 인원수 세팅
        System.out.println(message);
        long systemTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        String dTime = formatter.format(systemTime);
        message.setTimenow(dTime);
        System.out.println("DB 저장 완료");
        chatMessageRepository.save(message);
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message); // 메서드 일원화
        System.out.println("메세지 송부 요청 완료");
    }
}
