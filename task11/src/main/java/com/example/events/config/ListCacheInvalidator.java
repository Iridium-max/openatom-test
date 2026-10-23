package com.example.events.config;

import com.example.events.service.EventService.EventsChanged;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.*;

@Component
public class ListCacheInvalidator {
    // 分页会整体移动，新增/修改/取消/报名均可能影响任意页，因此清除此列表缓存区域。
    @CacheEvict(cacheNames="eventLists", allEntries=true)
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void invalidate(EventsChanged ignored) {}
}
