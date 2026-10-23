package com.example.events;

import com.example.events.api.Contracts.*;
import com.example.events.domain.Event;
import com.example.events.exception.BusinessException;
import com.example.events.repository.*;
import com.example.events.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")
class EventIntegrationTest {
    static final Instant NOW=Instant.parse("2030-01-01T00:00:00Z");
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UserService users;
    @Autowired EventService service;
    @Autowired EventRepository events;
    @Autowired RegistrationRepository registrations;
    @Autowired UserRepository userRepository;
    @Autowired ListRevision revision;
    @Autowired CacheManager caches;
    @MockitoBean Clock clock;

    @BeforeEach void reset(){
        when(clock.instant()).thenReturn(NOW);
        registrations.deleteAll(); events.deleteAll(); userRepository.deleteAll();
        caches.getCache("eventLists").clear();
    }
    EventInput input(String title,int capacity){ return new EventInput(title,"一起学习 Spring Boot","线上会议",Instant.parse("2035-05-01T10:00:00Z"),capacity); }
    void signup(String username){ users.signup(new Signup(username,"password123")); }
    MockHttpSession login(String username) throws Exception {
        return (MockHttpSession)mvc.perform(post("/api/auth/login").with(csrf()).param("username",username).param("password","password123"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200)).andReturn().getRequest().getSession(false);
    }

    @Test void guestBrowseAndUnifiedErrors() throws Exception {
        mvc.perform(get("/api/events")).andExpect(status().isOk()).andExpect(jsonPath("$.data.content").isArray());
        mvc.perform(get("/api/events/99999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value(404));
        mvc.perform(post("/api/events").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(input("活动",2))))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(401));
        mvc.perform(get("/api/events").param("page","-1")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400));
        mvc.perform(get("/api/events").param("size","101")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/events").param("status","WRONG")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/events").param("page","abc")).andExpect(status().isBadRequest());
    }
    @Test void csrfLoginSignupLogout() throws Exception {
        mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andExpect(jsonPath("$.data.token").isString());
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value(403));
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"alice\",\"password\":\"password123\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.data.passwordHash").doesNotExist());
        mvc.perform(post("/api/auth/login").with(csrf()).param("username","alice").param("password","bad"))
            .andExpect(status().isUnauthorized());
        MockHttpSession session=login("alice");
        mvc.perform(get("/api/auth/me").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.data.username").value("alice"));
        mvc.perform(post("/api/auth/logout").session(session).with(csrf())).andExpect(status().isOk());
        assertThat(userRepository.findByUsername("alice").orElseThrow().getPasswordHash()).startsWith("$2").doesNotContain("password123");
    }
    @Test void ownerOnlyUpdateAndCancel() throws Exception {
        signup("alice"); signup("bobby"); MockHttpSession alice=login("alice"), bob=login("bobby");
        var result=mvc.perform(post("/api/events").session(alice).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(input("读书会",3)))).andExpect(status().isCreated()).andReturn();
        long id=json.readTree(result.getResponse().getContentAsString()).at("/data/id").asLong();
        mvc.perform(put("/api/events/"+id).session(bob).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(input("篡改",3)))).andExpect(status().isForbidden()).andExpect(jsonPath("$.code").value(403));
        mvc.perform(delete("/api/events/"+id).session(bob).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(put("/api/events/"+id).session(alice).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(input("新标题",4)))).andExpect(status().isOk()).andExpect(jsonPath("$.data.title").value("新标题"));
        mvc.perform(delete("/api/events/"+id).session(alice).with(csrf())).andExpect(status().isOk());
        mvc.perform(get("/api/events/"+id)).andExpect(jsonPath("$.data.cancelled").value(true));
    }
    @Test void validationAndLiteralKeywordSearch() throws Exception {
        signup("alice"); MockHttpSession session=login("alice");
        mvc.perform(post("/api/events").session(session).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value(400));
        service.create(input("100% 技术",3),"alice"); service.create(input("普通活动",3),"alice");
        assertThat(service.list(0,9,"%",null).totalElements()).isEqualTo(1);
        assertThat(service.list(0,9,"spring",null).totalElements()).isEqualTo(2);
        assertThat(service.list(1,1,null,null).content()).hasSize(1);
    }
    @Test void cacheInvalidationCoversAllMutations(){
        signup("alice"); signup("bobby");
        assertThat(service.list(0,9,null,null).totalElements()).isZero();
        long before=revision.current();
        EventView event=service.create(input("缓存测试",2),"alice");
        assertThat(revision.current()).isGreaterThan(before);
        assertThat(service.list(0,9,null,null).totalElements()).isEqualTo(1);
        assertThat(service.list(0,9,"新标题",null).totalElements()).isZero();
        service.update(event.id(),input("新标题",2),"alice");
        assertThat(service.list(0,9,"新标题",null).totalElements()).isEqualTo(1);
        service.register(event.id(),"bobby");
        assertThat(service.list(0,9,null,null).content().get(0).registeredCount()).isEqualTo(1);
        service.cancel(event.id(),"alice");
        assertThat(service.list(0,9,null,"UPCOMING").totalElements()).isZero();
        assertThat(service.list(0,9,null,null).content().get(0).cancelled()).isTrue();
    }
    @Test void statusChangesWithoutAnyMutation(){
        signup("alice"); service.create(input("时间边界",2),"alice");
        assertThat(service.list(0,9,null,"UPCOMING").totalElements()).isEqualTo(1);
        when(clock.instant()).thenReturn(Instant.parse("2035-05-01T10:00:00Z"));
        assertThat(service.list(0,9,null,"UPCOMING").totalElements()).isZero();
        assertThat(service.list(0,9,null,"ENDED").totalElements()).isEqualTo(1);
    }
    @Test void duplicateFullCancelledAndCapacityGuard(){
        signup("alice"); signup("bobby"); signup("carol");
        var e=service.create(input("小活动",1),"alice"); service.register(e.id(),"bobby");
        assertThatThrownBy(()->service.register(e.id(),"bobby")).isInstanceOf(BusinessException.class).hasMessageContaining("重复");
        assertThatThrownBy(()->service.register(e.id(),"carol")).hasMessageContaining("已满");
        var bigger=service.create(input("大活动",2),"alice"); service.register(bigger.id(),"bobby"); service.register(bigger.id(),"carol");
        assertThatThrownBy(()->service.update(bigger.id(),input("缩容",1),"alice")).hasMessageContaining("名额");
        service.cancel(bigger.id(),"alice");
        assertThatThrownBy(()->service.register(bigger.id(),"alice")).hasMessageContaining("取消");
        assertThat(registrations.countByEventId(bigger.id())).isEqualTo(2);
    }
    @Test void pastEventCannotBeRegistered(){
        signup("alice"); var user=users.require("alice");
        var e=events.saveAndFlush(new Event(new EventInput("历史活动","已经结束","线上",NOW.minusSeconds(1),2),user,NOW));
        assertThatThrownBy(()->service.register(e.getId(),"alice")).hasMessageContaining("已开始或结束");
    }
    @Test void concurrentRegistrationNeverOversells() throws Exception {
        signup("owner"); for(int i=0;i<8;i++) signup("user"+i);
        var e=service.create(input("并发测试",3),"owner");
        ExecutorService pool=Executors.newFixedThreadPool(8); CountDownLatch start=new CountDownLatch(1);
        try {
            List<Future<Boolean>> results=new ArrayList<>();
            for(int i=0;i<8;i++) { String username="user"+i; results.add(pool.submit(()->{
                start.await(); try { service.register(e.id(),username); return true; }
                catch(BusinessException ex){ assertThat(ex.getStatus()).isEqualTo(409); return false; }
            })); }
            start.countDown(); int success=0;
            for(var future:results) if(future.get(20,TimeUnit.SECONDS)) success++;
            assertThat(success).isEqualTo(3);
            assertThat(registrations.countByEventId(e.id())).isEqualTo(3);
            assertThat(service.detail(e.id()).registeredCount()).isEqualTo(3);
        } finally { pool.shutdownNow(); }
    }
    @Test void concurrentDuplicateRegistrationCreatesOneRow() throws Exception {
        signup("alice"); var e=service.create(input("重复并发",10),"alice");
        ExecutorService pool=Executors.newFixedThreadPool(2); CountDownLatch start=new CountDownLatch(1);
        try {
            Callable<Boolean> task=()->{ start.await(); try { service.register(e.id(),"alice"); return true; } catch(BusinessException ex){ return false; } };
            Future<Boolean> a=pool.submit(task), b=pool.submit(task); start.countDown();
            assertThat(List.of(a.get(20,TimeUnit.SECONDS),b.get(20,TimeUnit.SECONDS))).containsExactlyInAnyOrder(true,false);
            assertThat(registrations.countByEventId(e.id())).isEqualTo(1);
            assertThat(service.detail(e.id()).registeredCount()).isEqualTo(1);
        } finally { pool.shutdownNow(); }
    }
}
