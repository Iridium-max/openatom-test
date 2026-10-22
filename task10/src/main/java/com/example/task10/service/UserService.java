package com.example.task10.service;

import com.example.task10.dto.AuthRequest;
import com.example.task10.dto.AuthResponse;
import com.example.task10.entity.User;
import com.example.task10.repository.UserRepository;
import com.example.task10.util.JwtUtil;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(AuthRequest request) {
        checkRequest(request);
        String username = request.username().trim();
        if (users.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRegisterTime(LocalDateTime.now());
        try {
            // 数据库唯一约束也能防止同时注册同一用户名。
            users.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            if (users.existsByUsername(username)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
            }
            throw e;
        }
        return AuthResponse.message("注册成功");
    }

    public AuthResponse login(String authorization, AuthRequest request) {
        if (hasValidToken(authorization)) {
            return AuthResponse.message("登录成功");
        }
        if (request == null || request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请使用用户名和密码登录");
        }
        checkRequest(request);
        User user = users.findByUsername(request.username().trim()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        return new AuthResponse("登录成功", jwtUtil.createToken(user.getUsername()),
                JwtUtil.EXPIRES_IN_SECONDS);
    }

    public boolean hasValidToken(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return false;
        }
        String username = jwtUtil.getUsername(authorization.substring(7).trim());
        return username != null && users.existsByUsername(username);
    }
    private void checkRequest(AuthRequest request) {
        if (request == null || request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名和密码不能为空");
        }
        if (request.username().trim().length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "用户名不能超过50个字符");
        }
        // BCrypt 最多处理 72 字节，中文也按 UTF-8 字节计数。
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "密码不能超过72个UTF-8字节");
        }
    }
}