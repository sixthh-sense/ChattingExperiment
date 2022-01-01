package com.example.demo.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private String secretKey = "jwt_secret_!@#$%"; // 123456bookmbtibookmbtisecretkey123456
    public static final String JWT_SECRET = "jwt_secret_!@#$%";

//    @Value("${spring.datasource.secretKey}")
//    private String secretKey;

    private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    // JWT 토큰의 유효기간: 7일 (단위: seconds)
    private static final int JWT_TOKEN_VALID_SEC = 7 * DAY;
    // JWT 토큰의 유효기간: 7일 (단위: milliseconds)
    private static final long JWT_TOKEN_VALID_MILLI_SEC = JWT_TOKEN_VALID_SEC * 1000L;

    private final UserDetailsService userDetailsService;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // public static Algorithm HMAC256(String secret) throws IllegalArgumentException {
    //        return new HMACAlgorithm("HS256", "HmacSHA256", secret);
    //    }

    public String createToken(String userPk, Long userId, String nickname) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("userId", userId); // 정보는 key / value 쌍으로 저장된다.
        claims.put("nickname", nickname);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + JWT_TOKEN_VALID_MILLI_SEC)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과 // or signWith(key, preferredSignatureAlgorithm) //.sign(Algorithm.HMAC256("jwtsecret!@#$%"));
                // signature 에 들어갈 secret값 세팅
                .compact();
        // https://stackoverflow.com/questions/40252903/static-secret-as-byte-key-or-string
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken);
            return !claims.getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}