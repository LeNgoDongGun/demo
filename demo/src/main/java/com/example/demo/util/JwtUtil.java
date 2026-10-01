package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Chuỗi bí mật ngẫu nhiên tối thiểu 32 ký tự để mã hóa HmacSHA256
    private final String SECRET_KEY_STRING = "A_VERY_SECRET_KEY_FOR_JWT_SPRING_BOOT_PRACTICE_32_CHARS";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    // Thời hạn Token: 10 phút (600,000 miligiây)
    private final long EXPIRATION_TIME = 600000;

    // 1. Sinh Token từ Username
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 2. Lấy Username từ Token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 3. Kiểm tra Token còn hạn và hợp lệ không
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}