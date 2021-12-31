package com.example.demo.handler;

import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChatMessageService;
import com.example.demo.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        System.out.println("웹소켓에 신호 들어옴");

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            System.out.println("웹소켓 연결 요청");

            String jwtToken = accessor.getFirstNativeHeader("token");
            System.out.println("토큰 확인 토큰 값:"+ jwtToken);

            System.out.println("연결 요청");
            log.info("CONNECT {}", jwtToken);

            System.out.println("토큰 유효성 검증");
            jwtTokenProvider.validateToken(jwtToken);
            System.out.println("CONNECT 완료");
        }
        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            //채팅룸 구독요청
            System.out.println("구독 요청");

            // header정보에서 구독 destination정보를 얻고, roomId를 추출한다.
            String roomId = chatMessageService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            System.out.println("해당 룸 ID:" +roomId);

            String sessionId = (String) message.getHeaders().get("simpSessionId");

            // 클라이언트 입장 메시지를 채팅방에 발송
            // 토큰 가져오기
            String jwtToken = accessor.getFirstNativeHeader("token");
            User user;
            if (jwtToken != null) { // 여기서의 jwtToken지정 userPk라는 건 kakao username, 즉 email
                user = userRepository.findByUsername(jwtTokenProvider.getUserPk(jwtToken))
                        .orElseThrow(
                                () -> new IllegalArgumentException("존재하지 않는 user입니다.")
                        );
            } else {
                throw new IllegalArgumentException("유효하지 않은 token입니다.");
            }
            // 유저 인덱스 추출
            Long userId = user.getId();
            chatRoomService.setUserEnterInfo(sessionId, roomId, userId);
            chatMessageService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .senderId(userId)
                    .build());
            log.info("SUBSCRIBED {}, {}", user.getUsername(), roomId);
        }
        return message;
    }
}