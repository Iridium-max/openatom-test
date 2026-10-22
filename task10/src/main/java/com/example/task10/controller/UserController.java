package com.example.task10.controller;

import com.example.task10.dto.AuthRequest;
import com.example.task10.dto.AuthResponse;
import com.example.task10.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;
    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody(required = false) AuthRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) String body) {
        // Token 优先，免密登录无需请求体，也不依赖其格式。
        if (userService.hasValidToken(authorization)) {
            return AuthResponse.message("登录成功");
        }
        AuthRequest request = null;
        if (body != null && !body.isBlank()) {
            try {
                request = objectMapper.readValue(body, AuthRequest.class);
            } catch (JsonProcessingException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "请求体必须是正确的JSON");
            }
        }
        return userService.login(null, request);
    }
}
