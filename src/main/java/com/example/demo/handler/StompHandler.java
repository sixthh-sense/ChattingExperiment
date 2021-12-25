package com.example.demo.handler;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.security.jwt.JwtDecoder;
import com.example.demo.security.provider.JWTAuthProvider;
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

    private final JwtDecoder jwtDecoder;
    private final JWTAuthProvider jwtAuthProvider;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // accessor는 StompHeaderAccessor를 message로 감쌌다? .wrap은 뭘까?
        // "Create an instance from the payload and headers of the given Message."라고 하네?

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);
            //jwtTokenProvider.validateToken(jwtToken);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
            // header 정보에서 구독 destination 정보를 얻고, roomId를 추출한다.
            String roomId = chatMessageService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));

            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 맵핑해 놓는다.(나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함)
            String sessionId = (String) message.getHeaders().get("simpSessionId");

            // 클라이언트 입장 메시지를 채팅방에 발송한다.(redis publish)
            //토큰 가져옴
            String jwtToken = accessor.getFirstNativeHeader("token");
            User user;
            if (jwtToken != null) {
                //토큰으로 user 가져옴
                user = userRepository.findByUsername(jwtDecoder.decodeUsername(jwtToken))
                        .orElseThrow(()->new IllegalArgumentException("user가 존재하지 않습니다."));

//                user = userRepository.findByEmail(jwtTokenProvider.getUserPk(jwtToken), User.class)
//                        .orElseThrow(()->new IllegalArgumentException("user 가 존재하지 않습니다."));
            }else {
                throw new IllegalArgumentException("유효하지 않은 token 입니다.");
            }
            Long userId = user.getId();
            chatRoomService.setUserEnterInfo(sessionId, roomId, userId);
            chatMessageService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .roomId(roomId)
                    .sender(user)
                    .build());
            log.info("SUBSCRIBED {}, {}", user.getUsername(), roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            // 연결이 종료된 클라이언트 sessionId 로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = chatRoomService.getUserEnterRoomId(sessionId);
            // 저장했던 sessionId 로 유저 객체를 받아옴
            User user = chatRoomService.chkSessionUser(sessionId);
            String username = user.getUsername();
            chatMessageService.sendChatMessage(ChatMessage.builder()
                    .type(ChatMessage.MessageType.QUIT)
                    .roomId(roomId)
                    .sender(user)
                    .build());
            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다.
            chatRoomService.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {}, {}", username, roomId);
            // 유저가 퇴장할 당시의 마지막 TALK 타입 메세지 id 를 저장함
            //allChatInfoService.updateReadMessage(user,roomId);
        }
        return message;



    }
}
