package com.example.demo.controller;

import com.example.demo.dto.HeaderDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.oauth2.UserDetailsImpl;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // 카카오 로그인
    // https://kauth.kakao.com/oauth/authorize?client_id=5d14d9239c0dbefee951a1093845427f&redirect_uri=http://localhost:8080/user/kakao/callback&response_type=code
    // https://kauth.kakao.com/oauth/authorize?client_id=5d14d9239c0dbefee951a1093845427f&redirect_uri=http://localhost:3000/user/kakao/callback&response_type=code

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public HeaderDto kakaoLogin(@RequestParam(value = "code") String code) {
        System.out.println("UserController received: " + code);
       return userService.kakaoLogin(code);
    }

    // 내정보 입력 / 수정
    @PutMapping("/api/profile")
    public void updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserRequestDto userRequestDto) {
        // 추가 정보 입력
        userService.updateProfile(userDetails.getUser(), userRequestDto);
    }

}
