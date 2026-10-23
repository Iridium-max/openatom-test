package com.example.events.service;

import com.example.events.api.Contracts.*;
import com.example.events.domain.User;
import com.example.events.exception.BusinessException;
import com.example.events.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;

@Service
public class UserService {
    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final Clock clock;
    public UserService(UserRepository users, PasswordEncoder passwords, Clock clock){
        this.users=users; this.passwords=passwords; this.clock=clock;
    }
    @Transactional
    public UserView signup(Signup input){
        if(input.password().getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72)
            throw new BusinessException(400, "密码的 UTF-8 编码不能超过72字节");
        if(users.existsByUsername(input.username())) throw new BusinessException(409, "用户名已存在");
        User user=users.saveAndFlush(new User(input.username(), passwords.encode(input.password()), clock.instant()));
        return new UserView(user.getId(), user.getUsername());
    }
    @Transactional(readOnly=true)
    public User require(String username){
        return users.findByUsername(username).orElseThrow(()->new BusinessException(401, "请先登录"));
    }
    public UserView me(String username){
        User user=require(username); return new UserView(user.getId(), user.getUsername());
    }
}
