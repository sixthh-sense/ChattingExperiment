package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatRoom {
    // redis에 저장되는 객체들은 Serialize가 가능해야 함. 그때는 implements Serializable 추가해야.

    // private static final long serialVersionUID = 6494678977089006639L; 숫자는 임의의 숫자로 해도 괜찮은 듯.
    // 참고 링크: https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=kkson50&logNo=220564273220

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String roomId; // auto-generate

    @Column
    private String name; // "채팅방의" 제목

//    @ManyToMany
//    @JoinColumn(name = "chat_room_user")
//    private List<User> user = new ArrayList<>();

    @Column
    private Long userCount = 0L; // 채팅방 인원수

//   public static ChatRoom create(String name) {
//        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.roomId = UUID.randomUUID().toString();
//        chatRoom.name = name;
//        return chatRoom;
//    }
}