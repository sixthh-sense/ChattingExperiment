package com.example.demo.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatRoom implements Serializable {
    // redis에 저장되는 객체들은 Serialize가 가능해야 함. 그때는 implements Serializable 추가해야.


    private static final long serialVersionUID = 6494678977089006639L; //숫자는 임의의 숫자로 해도 괜찮은 듯.
    // 참고 링크: https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=kkson50&logNo=220564273220
    // 반드시 serialVersionUID 라고 써야만 하는 건가?? 주황색 신기.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String roomId; // auto-generate -> roomUuid 건의하자.

    @Column
    private String name; // "채팅방의" 제목

    @Column
    private Long ownUserId; // 채팅방 만든 user의 id(pk). 한마디로 방장.

    public static ChatRoom create(String name, User user) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        chatRoom.ownUserId  = user.getId();
        return chatRoom;
    }
}