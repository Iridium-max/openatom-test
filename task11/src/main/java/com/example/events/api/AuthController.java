package com.example.events.api;

import com.example.events.api.Contracts.*;
import com.example.events.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final UserService users;
    public AuthController(UserService users){ this.users=users; }
    @GetMapping("/csrf")
    ApiResponse<Map<String,String>> csrf(CsrfToken token){
        return ApiResponse.ok(Map.of("headerName",token.getHeaderName(),"token",token.getToken()));
    }
    @PostMapping("/register")
    ResponseEntity<ApiResponse<UserView>> register(@Valid @RequestBody Signup input){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(users.signup(input)));
    }
    @GetMapping("/me")
    ApiResponse<UserView> me(Principal principal){ return ApiResponse.ok(users.me(principal.getName())); }
}
