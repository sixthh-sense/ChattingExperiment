package com.example.demo.repository;

import com.example.demo.model.ChatRoom;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAll();
    ChatRoom findByRoomId(String roomId);
    List<ChatRoom> findAllByUser(User user);
}
