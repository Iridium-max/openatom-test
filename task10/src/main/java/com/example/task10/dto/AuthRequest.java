package com.example.task10.dto;

// 注册、账号密码登录共用的 JSON 请求。
public record AuthRequest(String username, String password) {}