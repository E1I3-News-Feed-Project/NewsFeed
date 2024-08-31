package com.nbacm.newsfeed.domain.user.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000000L; // 6000분

    @Value("${jwt.secret.key}")// Base64 Encode 한 SecretKey
    private  String SECRET_KEY;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());

    }

    // JWT 토큰 생성
    public String generateToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(email)
                .claim(AUTHORIZATION_KEY,email)
                .setIssuedAt(now)

                .setExpiration(new Date(now.getTime() + TOKEN_TIME))
                .signWith(SignatureAlgorithm.HS256, getSecretKey())
                .compact();
    }

    // 토큰에서 사용자 이름 추출
    public String getUserEmailFromToken(String token) {
        return parseClaims(token)
                .getSubject();
    }

    // 사용자 역할을 JWT 토큰에서 추출하는 메서드
    public String getUserRoleFromToken(String token) {
        // JWT 토큰을 파싱하여 Claims 객체를 얻음
        Claims claims = parseClaims(token);
        return claims.get(AUTHORIZATION_KEY, String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        }
        catch (Exception e) {
            logger.warn("이미 만료된 토큰 입니다",e);
            return true;
        }
    }

    // JWT 토큰에서 Claims 추출
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .setAllowedClockSkewSeconds(60)
                .parseClaimsJws(token.replace(BEARER_PREFIX, ""))
                .getBody();
    }


}
