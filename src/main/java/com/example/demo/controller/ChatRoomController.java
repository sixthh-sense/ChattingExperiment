package com.example.demo.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class ChatRoomController {

    @GetMapping("/")
    @ResponseBody
    public String main() {
        return "test success";
    }
}