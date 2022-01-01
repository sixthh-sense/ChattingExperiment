package com.example.demo.oauth2;

import com.example.demo.dto.KakaoUserInfoDto;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

// @ConfigurationProperties
@Component
public class KakaoOAuth2 {
    public KakaoUserInfoDto getUserInfo(String authorizedCode) {
        // 1. 인가코드 -> 액세스 토큰
        String accessToken = getAccessToken(authorizedCode);
        // 2. 액세스 토큰 -> 카카오 사용자 정보
        return getUserInfoByToken(accessToken);
    }

    @Value("${spring.datasource.client_id}")
    private String client_id;

    @Value("${spring.datasource.redirect_uri}")
    private String redirect_uri;

    public String getAccessToken(String authorizedCode) {
        // HTTP Header object 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성 -> 오류 나서 다시 시도 body <-> params 둘 차이점은 대체 무엇? 별 차이 없나?
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", client_id);                  // 개발 REST API 키
        body.add("redirect_uri", redirect_uri);      // 개발 Redirect URI(BE local test)
        body.add("code", authorizedCode);

        // HTTP 요청 보내기
        RestTemplate rt = new RestTemplate();
        // baseballmate를 보고 추가한 부분. 무슨 의미인지 궁금.
        //rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        // JSON -> 액세스 토큰 파싱 -> 이쪽으로 바꿔도 되는 듯.
        String tokenJson = response.getBody();
        JSONObject rjson = new JSONObject(tokenJson);

        return rjson.getString("access_token");
    }

    private KakaoUserInfoDto getUserInfoByToken(String accessToken) {
        // HTTP Header object 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);      // JWT 토큰
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기  // HttpHeader와 HttpBody를 하나의 오브젝트에 담기?
        RestTemplate rt = new RestTemplate();
        // baseballmate보고 추가한 부분
        //rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);


        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me", // "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        // JSON -> Java Object
        // 이 부분에서 카톡 프로필 정보 가져옴
        JSONObject body = new JSONObject(response.getBody());
        //System.out.println(body);

        // ID (카카오 기본키)
        Long id = body.getLong("id");
        // 아이디 (이메일)
        String username = body.getJSONObject("kakao_account").getString("email");
        // 닉네임
        String nickname = body.getJSONObject("properties").getString("nickname");

        // profile_image_needs_agreement: true (이미지 동의 안함), false (이미지 동의)
        // is_default_image: true (기본 이미지), false (이미지 등록됨)
        // 프로필 이미지 -> 기본 이미지 등록할 거면 여기에서.
        String profileImage = "";
        // 이미지 동의 및 등록 되었으면
        if (!body.getJSONObject("kakao_account").getBoolean("profile_image_needs_agreement") &&
                !body.getJSONObject("kakao_account").getJSONObject("profile").getBoolean("is_default_image")) {
            profileImage = body.getJSONObject("kakao_account").getJSONObject("profile").getString("profile_image_url");
        }

        // has_gender: false (성별 선택 안함), true (성별 선택)
        // gender_needs_agreement: true (성별 동의 안함), false (성별 동의)
        // 성별 (male, female, unchecked)
        String gender = "";
        if (!body.getJSONObject("kakao_account").getBoolean("has_gender")) {
            gender = "unchecked";
        }
        // 성별 선택 및 성별 동의 되었으면
        if (body.getJSONObject("kakao_account").getBoolean("has_gender") &&
                !body.getJSONObject("kakao_account").getBoolean("gender_needs_agreement")) {
            gender = body.getJSONObject("kakao_account").getString("gender");
        }

        // age_range_needs_agreement: true (연령대 동의 안함), false (연령대 동의)
        // 연령대
        String ageRange = "";
        // 이미지 동의 및 등록 되었으면
        if (!body.getJSONObject("kakao_account").getBoolean("age_range_needs_agreement")) {
            ageRange = body.getJSONObject("kakao_account").getString("age_range");
        }
        return new KakaoUserInfoDto(
                id,
                username,
                nickname,
                profileImage,
                gender,
                ageRange);
    }


}
