package com.example.events.service;
import com.example.events.api.Contracts.EventView;
import com.example.events.domain.Event;
final class EventViews {
    private EventViews() {}
    static EventView from(Event e){
        return new EventView(e.getId(), e.getTitle(), e.getDescription(), e.getLocation(), e.getEventTime(),
            e.getCapacity(), e.getRegisteredCount(), e.isCancelled(), e.getOrganizer().getId(),
            e.getOrganizer().getUsername(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
