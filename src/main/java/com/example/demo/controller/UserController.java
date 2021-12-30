package com.example.demo.controller;

import com.example.demo.dto.HeaderDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.dto.UserResponseDto;
import com.example.demo.oauth2.UserDetailsImpl;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // 카카오 로그인
    // https://kauth.kakao.com/oauth/authorize?client_id=5d14d9239c0dbefee951a1093845427f&redirect_uri=http://localhost:8080/user/kakao/callback&response_type=code
//    @GetMapping("/user/kakao/callback")
//    public UserResponseDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
//        // 카카오 서버로부터 받은 인가 코드, JWT 토큰
//        //System.out.println(code); // O37QigD0-Msn5AdYWjXpzpxFI7UIc19cbIDzlZd_0nvIVbtD5Eqm0v8HmqPDof62GgH97wo9dVoAAAF99uRdiw -> 당연한 말이지만 userService의 kakaoLogin 입력변수 String code값과 같음
//        //System.out.println(response); // org.springframework.security.web.firewall.FirewalledResponse@43c2e924
//        return userService.kakaoLogin(code, response);
//    }

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    @ResponseBody
    public String kakaoLogin(@RequestParam(value = "code") String code) {
        userService.kakaoLogin(code);
        return "redirect:/chat/room";
    }


    // 내정보 입력 / 수정
    @PutMapping("/api/profile")
    public void updateProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserRequestDto userRequestDto) {
        // 추가 정보 입력
        userService.updateProfile(userDetails.getUser(), userRequestDto);
    }

}
