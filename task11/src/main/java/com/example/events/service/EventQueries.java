package com.example.events.service;

import com.example.events.api.Contracts.*;
import com.example.events.exception.EventNotFoundException;
import com.example.events.repository.EventRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class EventQueries {
    private final EventRepository events;
    public EventQueries(EventRepository events){ this.events=events; }
    // 不缓存 JPA 实体/PageImpl，避免懒加载、序列化和泄露内部字段。
    @Cacheable(cacheNames="eventLists", key="{#revision, #page, #size, #keyword, #status, #second}")
    @Transactional(readOnly=true)
    public PageView<EventView> list(long revision, int page, int size, String keyword, String status, long second){
        String pattern="%"+keyword.replace("!", "!!").replace("%", "!%").replace("_", "!_")+"%";
        var result=events.search(keyword, pattern, status, Instant.ofEpochSecond(second),
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id")));
        return new PageView<>(result.map(EventViews::from).getContent(), page, size,
            result.getTotalElements(), result.getTotalPages());
    }
    @Transactional(readOnly=true)
    public EventView detail(Long id){ return EventViews.from(events.findById(id).orElseThrow(EventNotFoundException::new)); }
}
