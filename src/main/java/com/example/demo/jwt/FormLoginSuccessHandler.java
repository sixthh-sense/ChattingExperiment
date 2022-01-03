package com.example.demo.jwt;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private JwtTokenProvider jwtTokenProvider;

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "BEARER";

    public FormLoginSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) {
        final String username = authentication.getName();
        // Token 생성
        final String token = jwtTokenProvider.generateToken(username);
        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);
    }

}