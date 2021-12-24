package com.example.demo.repository;

import com.example.demo.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAll();
    ChatRoom findByRoomId(String roomId);
}
