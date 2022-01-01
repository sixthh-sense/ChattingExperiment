package com.example.demo.service;

import com.example.demo.dto.HeaderDto;
import com.example.demo.oauth2.KakaoOAuth2;
import com.example.demo.jwt.JwtTokenProvider;
import com.example.demo.dto.KakaoUserInfoDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoOAuth2 kakaoOAuth2;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    static boolean signStatus = false;      // 회원가입 상태

    //카카오 로그인
    public HeaderDto kakaoLogin(String authorizedCode) { // throws JsonProcessingException
        System.out.println("authorizedCode: " + authorizedCode);

        KakaoUserInfoDto userInfo = kakaoOAuth2.getUserInfo(authorizedCode);
        System.out.println("KakaoUserInfoDto: " + userInfo);

        Long kakaoId = userInfo.getId();
        System.out.println("KakaoId: " + kakaoId);

        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        // nullable = false
        String username = userInfo.getUsername();                  // 카카오 아이디 (이메일)
        String password = UUID.randomUUID().toString();                 // 카카오 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        String nickname = userInfo.getNickname();                  // 카카오 닉네임

        System.out.println("KakaoUsername: " + username);
        System.out.println("KakaoNickname: " + nickname);

        // nullable = true
        String profileImage = userInfo.getProfileImage();          // 카카오 프로필 이미지
        String gender = userInfo.getGender();                      // 카카오 성별
        String ageRange = userInfo.getAgeRange().substring(0, 2);  // 카카오 연령대

        // 가입 여부
        if (kakaoUser == null) {
            kakaoUser = User.builder()
                    .kakaoId(kakaoId)
                    .username(username)
                    .password(encodedPassword)
                    .nickname(nickname)
                    .profileImage(profileImage)
                    .gender(gender)
                    .ageRange(ageRange)
                    .build();
            userRepository.save(kakaoUser);
            signStatus = false;                 // 처음 가입하면 false => 추가 정보 입력 페이지로 이동
        } else {
            signStatus = true;                  // 이미 가입했으면 true => 메인 페이지로 이동
        }

        // 로그인 처리
        Authentication kakaoUsernamePassword = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(kakaoUsernamePassword);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HeaderDto headerDto = new HeaderDto();

        // 로그인 처리 후 해당 유저 정보를 바탕으로 JWT토큰을 발급하고 해당 토큰을 Dto에 담아서 넘김
        User member = userRepository.findByKakaoId(kakaoId).orElseThrow(()
                -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        headerDto.setTOKEN(jwtTokenProvider.createToken(username, member.getId(), member.getNickname()));

        System.out.println("jwtTokenProvider token(email, id, nickname): " + jwtTokenProvider.createToken(username, member.getId(), member.getNickname()));

        return headerDto;
    }

    // 추가 정보 입력
    @Transactional
    public void updateProfile(User user, UserRequestDto userRequestDto) {
        // 사용자 조회
        User findUser = userRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다.")
        );

        // 닉네임 필수값이므로, null 값이면 카카오 닉네임으로 설정
        if (userRequestDto.getNickname() == null) {
            userRequestDto.setNickname(user.getNickname());
        }

        // 추가정보 설정하여 업데이트 (닉네임, 프로필, 소개글, 위치, 관심사, mbti)
        findUser.update(userRequestDto);

        // DB 저장
        userRepository.save(findUser);
    }

    @Transactional
    public User getUser(Long senderId) {
        return userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
    }
}