package com.example.demo.middleware;

import com.example.demo.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Bỏ qua kiểm tra Token đối với các API public (Login & Auth)
        if (path.startsWith("/login") || path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Kiểm tra Header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                // Token hợp lệ -> Cho phép đi tiếp tới Controller
                filterChain.doFilter(request, response);
                return;
            }
        }

        // Token không hợp lệ hoặc thiếu -> Trả về lỗi 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("401 Unauthorized: Bạn cần truyền JWT Token hợp lệ để truy cập API này!");
    }
}