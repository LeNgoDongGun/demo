package com.example.demo.controller;

import com.example.demo.model.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    // Giả lập danh sách người dùng mẫu (Database)
    // Tài khoản: admin | Mật khẩu: 123456
    private final User mockUser = new User(1, "admin", "123456");

    // API 1: Đăng nhập & Sinh JWT Token (POST /login)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Kiểm tra tài khoản và mật khẩu
        if (mockUser.getUserName().equals(loginRequest.getUserName()) &&
                mockUser.getPassword().equals(loginRequest.getPassword())) {

            // Sinh JWT Token
            String token = jwtUtil.generateToken(loginRequest.getUserName());
            mockUser.setToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng nhập thành công!");
            response.put("token", token);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác!");
    }

    // API 2: Xác thực Token (GET /auth)
    @GetMapping("/auth")
    public ResponseEntity<?> verifyToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                Map<String, Object> response = new HashMap<>();
                response.put("status", "VALID");
                response.put("userName", username);
                response.put("message", "Token hợp lệ!");
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ hoặc đã hết hạn!");
    }
}