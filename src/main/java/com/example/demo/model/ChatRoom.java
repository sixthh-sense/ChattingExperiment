package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatRoom {
    // redis에 저장되는 객체들은 Serialize가 가능해야 함, -> Serializable 참조

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String roomId; // auto-generate

    @Column
    private String name;

    @ManyToMany
    @JoinColumn(name = "chat_room_user")
    private List<User> user = new ArrayList<>();

    @Column
    private Long userCount = 0L; // 채팅방 인원수 (일단 1:1 고정 목표)


//    public static ChatRoom create(String name) {
//        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.roomId = UUID.randomUUID().toString();
//        chatRoom.name = name;
//        return chatRoom;
//    }
}