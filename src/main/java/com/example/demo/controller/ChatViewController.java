package com.example.demo.controller;

import com.example.demo.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatViewController {

    private final ChatRoomService chatRoomService;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms() {
        return "/room";
    }

    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/roomdetail";
    }
}
