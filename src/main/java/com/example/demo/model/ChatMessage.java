package com.example.demo.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter // RedisSubscriber.java 에서 필요로 함
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatMessage {

    public enum MessageType {
        ENTER, QUIT, TALK
    }

    @Builder
    public ChatMessage(MessageType type, String roomId, User sender, String message) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(value = EnumType.STRING)
    private MessageType type; // 메시지 타입

    @Column
    private String roomId; // 방번호

    @ManyToOne
    private User sender; // 메시지 보낸사람

//    @Column
//    private String username;

    //private String profileImage;

    @Column
    private String message; // 메시지

    @Column
    private String timenow;

}
