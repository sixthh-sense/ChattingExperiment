package com.example.demo.model;

import com.example.demo.dto.ChatMessageRequestDto;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
public class ChatMessage extends Timestamped {

    public enum MessageType {
        ENTER, TALK, LEAVE // QUIT 대신 LEAVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private MessageType type; // 강퇴기능 없는 버전. 1:1인데 강퇴라고 말하긴 좀.

    @Column
    private String roomId; // 방"번호"라고 해서 숫자가 아니다!! 임의로 생성된 문자열이다!!

    @Column(columnDefinition = "LONGTEXT")
    private String message; // LONGTEXT 처리를 해줘야 할 듯

    @Column
    private Long senderId; // 보낸 사람. userId(pk)

    public ChatMessage(ChatMessageRequestDto chatMessageRequestDto, User sender) {
        this.type = chatMessageRequestDto.getType();
        this.roomId = chatMessageRequestDto.getRoomId();
        this.senderId =  sender.getId(); // 이 부분이 bookback과 다른 부분.
        this.message = chatMessageRequestDto.getMessage();
    }
}
