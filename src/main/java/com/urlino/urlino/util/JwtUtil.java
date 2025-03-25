package com.urlino.urlino.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "YzuB7VJUrO5o7UJsq9nMXH6HNbJcJ7k8irVYd0oJ8dE=";
    private static final long EXPIRATION_TIME = 86400000;  // 24 小时

    /**
     * 生成 JWT
     */
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)  // 使用 userId 作为主题
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 从 JWT 中提取用户 ID
     */
    public String extractUserId(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 验证 JWT
     */
    public boolean validateToken(String token, String userId) {
        return extractUserId(token).equals(userId) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}