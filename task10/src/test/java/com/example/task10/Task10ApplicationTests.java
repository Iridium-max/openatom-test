package com.example.task10;

import com.example.task10.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
// 无需 Mockito；仅启用依赖注入，避免 Windows 测试代理的动态附加。
@org.springframework.test.context.TestExecutionListeners(
        listeners = org.springframework.test.context.support.DependencyInjectionTestExecutionListener.class,
        inheritListeners = false)
class Task10ApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    @Autowired ObjectMapper mapper;
    private static final String BODY = "{\"username\":\"student\",\"password\":\"hello123\"}";
    private static final String SECRET = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @BeforeEach void clean() { users.deleteAll(); }

    private void register() throws Exception {
        mvc.perform(post("/register").contentType("application/json").content(BODY))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test void registerStoresHashAndTimeAndRejectsDuplicate() throws Exception {
        register();
        var user = users.findByUsername("student").orElseThrow();
        assertNotEquals("hello123", user.getPassword());
        assertTrue(encoder.matches("hello123", user.getPassword()));
        assertNotNull(user.getRegisterTime());
        mvc.perform(post("/register").contentType("application/json").content(BODY))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test void passwordLoginIssuesSevenDayJwtAndTokenLoginNeedsNoBody() throws Exception {
        register();
        String json = mvc.perform(post("/login").contentType("application/json").content(BODY))
                .andExpect(status().isOk()).andExpect(jsonPath("$.expiresIn").value(604800))
                .andReturn().getResponse().getContentAsString();
        String token = mapper.readTree(json).get("token").asText();
        var claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .build().parseSignedClaims(token).getPayload();
        assertEquals("student", claims.getSubject());
        mvc.perform(post("/login").header("Authorization", "Bearer " + token)
                        .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().isOk());
        mvc.perform(post("/login").header("Authorization", "Bearer " + token)
                        .contentType("application/json").content("{broken"))
                .andExpect(status().isOk());
        assertEquals(604800000L, claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
        mvc.perform(post("/login").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.token").doesNotExist());
        mvc.perform(post("/login").header("Authorization", "Bearer " + token)
                        .contentType("application/json").content("{\"username\":\"wrong\",\"password\":\"wrong\"}"))
                .andExpect(status().isOk());
    }

    @Test void rejectsWrongPasswordAndSqlInjection() throws Exception {
        register();
        for (String body : new String[]{
                "{\"username\":\"student\",\"password\":\"wrong\"}",
                "{\"username\":\"' OR 1=1 --\",\"password\":\"hello123\"}"}) {
            mvc.perform(post("/login").contentType("application/json").content(body))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("用户名或密码错误"));
        }
    }

    @Test void rejectsInvalidExpiredAndForgedTokensButAllowsPasswordFallback() throws Exception {
        register();
        var key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        String expired = Jwts.builder().subject("student")
                .expiration(Date.from(Instant.now().minusSeconds(60))).signWith(key).compact();
        String forged = Jwts.builder().subject("student")
                .expiration(Date.from(Instant.now().plusSeconds(600)))
                .signWith(Jwts.SIG.HS256.key().build()).compact();
        String noExpiration = Jwts.builder().subject("student").signWith(key).compact();
        for (String token : new String[]{"invalid", expired, forged, noExpiration}) {
            mvc.perform(post("/login").header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }
        mvc.perform(post("/login").header("Authorization", "Bearer " + expired)
                        .contentType("application/json").content(BODY))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").isString());
        mvc.perform(post("/login")).andExpect(status().isUnauthorized());
    }

    @Test void validatesInputAndJson() throws Exception {
        mvc.perform(post("/register")).andExpect(status().isBadRequest());
        mvc.perform(post("/register").contentType("application/json")
                        .content("{\"username\":\" \",\"password\":\"abc\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/register").contentType("application/json")
                        .content(mapper.writeValueAsString(java.util.Map.of("username", "student",
                                "password", "密".repeat(25)))))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/register").contentType("application/json").content("{broken"))
                .andExpect(status().isBadRequest());
    }

    @Test void tokenForDeletedUserIsRejected() throws Exception {
        register();
        String json = mvc.perform(post("/login").contentType("application/json").content(BODY))
                .andReturn().getResponse().getContentAsString();
        String token = mapper.readTree(json).get("token").asText();
        users.deleteAll();
        mvc.perform(post("/login").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
