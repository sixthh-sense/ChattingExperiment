package com.example.demo.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class ChatMessage extends Timestamped {

    public enum MessageType {
        ENTER, LEAVE, TALK // QUIT 대신 LEAVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private MessageType type; // 강퇴기능 없는 버전. 1:1인데 강퇴라고 말하긴 좀.

    @Column
    private String roomId; // 방"번호"라고 해서 숫자가 아니다!! 임의로 생성된 문자열이다!!

//    @Column
//    private String nickname; // username이 좀더 정확하긴 하지만 식별용으론 nickname이 더 적합한 듯. 보낸 사람의 nickname
//
//    @Column(columnDefinition = "LONGTEXT")
//    private String profileImage; // 말 그대로 profileImage의 url. 길어서 LONGTEXT 처리.

    @Column(columnDefinition = "LONGTEXT")
    private String message; // LONGTEXT 처리를 해줘야 할 듯

    @Column
    private String username;


//    @ManyToOne //
//    @JoinColumn
//    private User sender;

//    @Builder
//    public ChatMessage(MessageType type, String roomId, String message, User sender) {
//        this.type = type;
//        this.roomId = roomId;
//        this.message = message;
//        this.sender = sender;
//    }
//
//    @Builder
//    public ChatMessage(ChatMessageRequestDto requestDto, UserService userService) {
//        this.type = requestDto.getType();
//        this.roomId = requestDto.getRoomId();
//        this.message = requestDto.getMessage();
//        this.sender = userService.getUser(requestDto.getSenderId());
//    }

//    @Column
//    private String createdAt; // 메세지의 생성시각
}
