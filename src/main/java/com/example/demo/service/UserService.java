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
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoOAuth2 kakaoOAuth2;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    static boolean signStatus;      // 회원가입 상태

    //카카오 로그인
    public HeaderDto kakaoLogin(String authorizedCode) { // throws JsonProcessingException

        KakaoUserInfoDto userInfo = kakaoOAuth2.getUserInfo(authorizedCode);

        Long kakaoId = userInfo.getId();
        User kakaoUser = userRepository.findByKakaoId(kakaoId)
                .orElse(null);

        // nullable = false
        String username = userInfo.getUsername();                  // 카카오 아이디 (이메일)
        String password = UUID.randomUUID().toString();                 // 카카오 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
        String nickname = userInfo.getNickname();                  // 카카오 닉네임

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

        return headerDto;
    }



        // 1. "인가 코드"로 "액세스 토큰" 요청 , HttpServletResponse response
        //String accessToken = getAccessToken(code);
        //System.out.println(code); // O37QigD0-Msn5AdYWjXpzpxFI7UIc19cbIDzlZd_0nvIVbtD5Eqm0v8HmqPDof62GgH97wo9dVoAAAF99uRdiw
        //System.out.println(accessToken); // zqLQZT1p5I8RcEc80rDcN6kVhfpL6dU42QIKuwo9dNsAAAF99uRgDw

        // 2. "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        //KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        //System.out.println(kakaoUserInfo); // KakaoUserInfoDto(id=2047375568, username=jeranum8@kakao.com, nickname=황소, profileImage=, gender=, ageRange=30~39)

        // 3. "카카오 사용자 정보"로 필요시 회원가입
        //User kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);
        //System.out.println(kakaoUser); // com.example.demo.model.User@3de7374a

        // 4. 강제 로그인 처리
       // return forceLogin(kakaoUser, response);
   // }

    // 1. "인가 코드"로 "액세스 토큰" 요청
//    private String getAccessToken(String code) throws JsonProcessingException {
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // HTTP Body 생성
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "authorization_code");
//        body.add("client_id", "5d14d9239c0dbefee951a1093845427f");                  // 개발 REST API 키
//        body.add("redirect_uri", "http://localhost:8080/user/kakao/callback");      // 개발 Redirect URI
//        body.add("code", code);
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
//                new HttpEntity<>(body, headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class
//        );
//
//        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
//        // JSON -> Java Object
////        String responseBody = response.getBody();
////        ObjectMapper objectMapper = new ObjectMapper();
////        JsonNode jsonNode = objectMapper.readTree(responseBody);
////        //System.out.println(jsonNode.get("access_token").asText()); -> 당연한 말이지만(?) 저 위의 accessToken과 같은 값
////        return jsonNode.get("access_token").asText();
//
//        // JSON -> 액세스 토큰 파싱 -> 이쪽으로 바꿔도 되는 듯.
//        String tokenJson = response.getBody();
//        JSONObject rjson = new JSONObject(tokenJson);
//
//        return rjson.getString("access_token");
//    }

    // 2. "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
  //  private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
//        // HTTP Header 생성
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Authorization", "Bearer " + accessToken);      // JWT 토큰
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        // HTTP 요청 보내기
//        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> response = rt.exchange(
//                "https://kapi.kakao.com/v2/user/me",
//                HttpMethod.POST,
//                kakaoUserInfoRequest,
//                String.class
//        );
//
//        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
//        // JSON -> Java Object
//        // 이 부분에서 카톡 프로필 정보 가져옴
//        JSONObject body = new JSONObject(response.getBody());
//        //System.out.println(body);
//
//        // ID (카카오 기본키)
//        Long id = body.getLong("id");
//        // 아이디 (이메일)
//        String username = body.getJSONObject("kakao_account").getString("email");
//        // 닉네임
//        String nickname = body.getJSONObject("properties").getString("nickname");
//
//        // profile_image_needs_agreement: true (이미지 동의 안함), false (이미지 동의)
//        // is_default_image: true (기본 이미지), false (이미지 등록됨)
//        // 프로필 이미지 -> 기본 이미지 등록할 거면 여기에서.
//        String profileImage = "";
//        // 이미지 동의 및 등록 되었으면
//        if (!body.getJSONObject("kakao_account").getBoolean("profile_image_needs_agreement") &&
//                !body.getJSONObject("kakao_account").getJSONObject("profile").getBoolean("is_default_image")) {
//            profileImage = body.getJSONObject("kakao_account").getJSONObject("profile").getString("profile_image_url");
//        }
//
//        // has_gender: false (성별 선택 안함), true (성별 선택)
//        // gender_needs_agreement: true (성별 동의 안함), false (성별 동의)
//        // 성별 (male, female, unchecked)
//        String gender = "";
//        if (!body.getJSONObject("kakao_account").getBoolean("has_gender")) {
//            gender = "unchecked";
//        }
//        // 성별 선택 및 성별 동의 되었으면
//        if (body.getJSONObject("kakao_account").getBoolean("has_gender") &&
//                !body.getJSONObject("kakao_account").getBoolean("gender_needs_agreement")) {
//            gender = body.getJSONObject("kakao_account").getString("gender");
//        }
//
//        // age_range_needs_agreement: true (연령대 동의 안함), false (연령대 동의)
//        // 연령대
//        String ageRange = "";
//        // 이미지 동의 및 등록 되었으면
//        if (!body.getJSONObject("kakao_account").getBoolean("age_range_needs_agreement")) {
//            ageRange = body.getJSONObject("kakao_account").getString("age_range");
//        }
//
//        return KakaoUserInfoDto.builder() // 이 부분은 그대로 놔둬도 될 듯?
//                .id(id)
//                .username(username)
//                .nickname(nickname)
//                .profileImage(profileImage)
//                .gender(gender)
//                .ageRange(ageRange)
//                .build();
 //   }

    // 3. "카카오 사용자 정보"로 필요시 회원가입
   // private User registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
//        Long kakaoId = kakaoUserInfo.getId();
//        User kakaoUser = userRepository.findByKakaoId(kakaoId)
//                .orElse(null);
//
//        // nullable = false
//        String username = kakaoUserInfo.getUsername();                  // 카카오 아이디 (이메일)
//        String password = UUID.randomUUID().toString();                 // 카카오 비밀번호 암호화
//        String encodedPassword = passwordEncoder.encode(password);
//        String nickname = kakaoUserInfo.getNickname();                  // 카카오 닉네임
//
//        // nullable = true
//        String profileImage = kakaoUserInfo.getProfileImage();          // 카카오 프로필 이미지
//        String gender = kakaoUserInfo.getGender();                      // 카카오 성별
//        String ageRange = kakaoUserInfo.getAgeRange().substring(0, 2);  // 카카오 연령대
//
//        // 가입 여부
//        if (kakaoUser == null) {
//            kakaoUser = User.builder()
//                    .kakaoId(kakaoId)
//                    .username(username)
//                    .password(encodedPassword)
//                    .nickname(nickname)
//                    .profileImage(profileImage)
//                    .gender(gender)
//                    .ageRange(ageRange)
//                    .build();
//            userRepository.save(kakaoUser);
//            signStatus = false;                 // 처음 가입하면 false => 추가 정보 입력 페이지로 이동
//        } else {
//            signStatus = true;                  // 이미 가입했으면 true => 메인 페이지로 이동
//        }
//
//        return kakaoUser;
  //  }

    // 4. 강제 로그인 처리
//    private UserResponseDto forceLogin(User kakaoUser, HttpServletResponse response) {
//        UserDetailsImpl userDetails = new UserDetailsImpl(kakaoUser);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // JWT 토큰 생성
//        String token = JwtTokenUtils.generateJwtToken(userDetails);
//        // System.out.println(token); // -> 내가 response header Authorization에서 BEARER 빼고 보는 녀석. 아래에서 response로 보낸다고 보이네.
//
//        // 헤더에 JWT 토큰 담아서 응답
//        response.addHeader("Authorization", "Bearer " + token);
//
//        return UserResponseDto.builder()
//                .nickname(userDetails.getUser().getNickname())
//                .profileImage(userDetails.getUser().getProfileImage())
//                .gender(userDetails.getUser().getGender())
//                .ageRange(userDetails.getUser().getAgeRange())
//                .intro(userDetails.getUser().getIntro())
//                .location(userDetails.getUser().getLocation())
//                .interest(userDetails.getUser().getInterest())
//                .mbti(userDetails.getUser().getMbti())
//                .signStatus(signStatus)
//                .build();
//    }

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

//    public User getUser(Long senderId) { // #1 여기서 senderId를 못 받아옴. 왜??
//        return userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
//    }
}