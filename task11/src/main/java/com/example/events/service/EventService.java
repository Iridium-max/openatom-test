package com.example.events.service;

import com.example.events.api.Contracts.*;
import com.example.events.domain.*;
import com.example.events.exception.*;
import com.example.events.repository.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.util.Locale;

@Service
public class EventService {
    private final EventRepository events;
    private final RegistrationRepository registrations;
    private final UserService users;
    private final ListRevision revision;
    private final EventQueries queries;
    private final Clock clock;
    private final ApplicationEventPublisher publisher;
    public EventService(EventRepository events, RegistrationRepository registrations, UserService users,
        ListRevision revision, EventQueries queries, Clock clock, ApplicationEventPublisher publisher){
        this.events=events; this.registrations=registrations; this.users=users;
        this.revision=revision; this.queries=queries; this.clock=clock; this.publisher=publisher;
    }
    public PageView<EventView> list(int page, int size, String keyword, String status){
        if(page<0 || size<1 || size>100) throw new BusinessException(400, "page 从0开始，size范围为1至100");
        String key=keyword==null?"":keyword.strip().toLowerCase(Locale.ROOT);
        String state=status==null?"":status.strip().toUpperCase(Locale.ROOT);
        if(key.length()>100) throw new BusinessException(400, "keyword 最多100个字符");
        if(!state.isEmpty() && !state.equals("UPCOMING") && !state.equals("ENDED"))
            throw new BusinessException(400, "status 只支持 UPCOMING 或 ENDED");
        // 无状态筛选不依赖时间；有状态筛选按秒区分，活动时间也统一保存至秒。
        return queries.list(revision.current(), page, size, key, state, state.isEmpty()?0:clock.instant().getEpochSecond());
    }
    public EventView detail(Long id){ return queries.detail(id); }
    @Transactional
    public EventView create(EventInput input, String username){
        validateTime(input);
        var event=events.save(new Event(input, users.require(username), clock.instant()));
        changed(); return EventViews.from(event);
    }
    @Transactional
    public EventView update(Long id, EventInput input, String username){
        Event event=owned(id, username);
        if(event.isCancelled()) throw new BusinessException(409, "已取消活动不能修改");
        if(!event.getEventTime().isAfter(clock.instant())) throw new BusinessException(409, "已结束活动不能修改");
        if(input.capacity()<event.getRegisteredCount()) throw new BusinessException(409, "名额不能小于已报名人数");
        validateTime(input);
        event.update(input, clock.instant()); changed(); return EventViews.from(event);
    }
    @Transactional
    public void cancel(Long id, String username){
        Event event=owned(id, username);
        if(!event.isCancelled()){ event.cancel(clock.instant()); changed(); }
    }
    @Transactional
    public RegistrationView register(Long id, String username){
        // 所有容量/状态修改与报名都锁同一活动行，锁保持到事务提交。
        Event event=events.findLocked(id).orElseThrow(EventNotFoundException::new);
        User user=users.require(username);
        if(event.isCancelled()) throw new BusinessException(409, "活动已取消");
        if(!event.getEventTime().isAfter(clock.instant())) throw new BusinessException(409, "活动已开始或结束，无法报名");
        if(registrations.existsByUserIdAndEventId(user.getId(), id)) throw new BusinessException(409, "请勿重复报名");
        if(event.getRegisteredCount()>=event.getCapacity()) throw new BusinessException(409, "报名人数已满");
        Registration registration=registrations.saveAndFlush(new Registration(user, event, clock.instant()));
        event.register(clock.instant()); changed();
        return new RegistrationView(registration.getId(), id, user.getId(), registration.getCreatedAt());
    }
    private Event owned(Long id, String username){
        Event event=events.findLocked(id).orElseThrow(EventNotFoundException::new);
        if(!event.getOrganizer().getId().equals(users.require(username).getId())) throw new UnauthorizedAccessException();
        return event;
    }
    private void validateTime(EventInput input){
        if(!input.eventTime().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).isAfter(clock.instant()))
            throw new BusinessException(400, "活动时间必须晚于当前时间");
    }
    private void changed(){ revision.bump(); publisher.publishEvent(new EventsChanged()); }
    public record EventsChanged() {}
}
