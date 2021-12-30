package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {
    private String roomId; // auto-generate -> roomUuid 건의하자.
    private String name; // "채팅방의" 제목
    private Long ownUserId;

}
