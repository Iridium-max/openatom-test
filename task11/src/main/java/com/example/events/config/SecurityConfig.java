package com.example.events.config;

import com.example.events.api.ApiResponse;
import com.example.events.repository.UserRepository;
import com.example.events.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
    @Bean UserDetailsService userDetailsService(UserRepository users){
        return username->users.findByUsername(username)
            .map(u->User.withUsername(u.getUsername()).password(u.getPasswordHash()).roles("USER").build())
            .orElseThrow(()->new UsernameNotFoundException("用户名或密码错误"));
    }
    @Bean SecurityFilterChain security(HttpSecurity http, ObjectMapper json, UserService users) throws Exception {
        // 保留默认 CSRF 防护；前端通过 /api/auth/csrf 获取令牌。
        var denied=(org.springframework.security.web.access.AccessDeniedHandler)(request,response,e)->
            write(response,json,403,ApiResponse.error(403,"无权访问或 CSRF 令牌无效，请刷新后重试"));
        http.authorizeHttpRequests(auth->auth
            .requestMatchers("/", "/index.html", "/app.js", "/style.css", "/favicon.ico", "/error").permitAll()
            .requestMatchers(HttpMethod.GET,"/api/events", "/api/events/*", "/api/auth/csrf").permitAll()
            .requestMatchers(HttpMethod.POST,"/api/auth/register", "/api/auth/login").permitAll()
            .anyRequest().authenticated())
            .exceptionHandling(e->e
                .authenticationEntryPoint((request,response,ex)->write(response,json,401,ApiResponse.error(401,"请先登录")))
                .accessDeniedHandler(denied))
            .formLogin(form->form.loginProcessingUrl("/api/auth/login")
                .successHandler((request,response,a)->write(response,json,200,ApiResponse.ok(users.me(a.getName()))))
                .failureHandler((request,response,e)->write(response,json,401,ApiResponse.error(401,"用户名或密码错误"))))
            .logout(logout->logout.logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request,response,a)->write(response,json,200,ApiResponse.ok(null))))
            .requestCache(cache->cache.disable());
        return http.build();
    }
    private static void write(HttpServletResponse response,ObjectMapper json,int status,Object body) throws IOException {
        response.setStatus(status); response.setContentType("application/json;charset=UTF-8");
        json.writeValue(response.getWriter(),body);
    }
}
