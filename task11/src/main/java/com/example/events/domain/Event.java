package com.example.events.domain;

import com.example.events.api.Contracts.EventInput;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity @Table(name="events")
public class Event {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, length=120) private String title;
    @Column(nullable=false, length=10000) private String description;
    @Column(nullable=false, length=255) private String location;
    @Column(nullable=false) private Instant eventTime;
    @Column(nullable=false) private int capacity;
    @Column(nullable=false) private int registeredCount;
    @Column(nullable=false) private boolean cancelled;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="organizer_id") private User organizer;
    @Column(nullable=false, updatable=false) private Instant createdAt;
    @Column(nullable=false) private Instant updatedAt;
    protected Event() {}
    public Event(EventInput input, User organizer, Instant now) {
        this.organizer=organizer; this.createdAt=now; update(input, now);
    }
    public void update(EventInput input, Instant now) {
        title=input.title().trim(); description=input.description().trim(); location=input.location().trim();
        eventTime=input.eventTime().truncatedTo(ChronoUnit.SECONDS); capacity=input.capacity(); updatedAt=now;
    }
    public void cancel(Instant now) { cancelled=true; updatedAt=now; }
    public void register(Instant now) { registeredCount++; updatedAt=now; }
    public Long getId(){ return id; }
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public String getLocation(){ return location; }
    public Instant getEventTime(){ return eventTime; }
    public int getCapacity(){ return capacity; }
    public int getRegisteredCount(){ return registeredCount; }
    public boolean isCancelled(){ return cancelled; }
    public User getOrganizer(){ return organizer; }
    public Instant getCreatedAt(){ return createdAt; }
    public Instant getUpdatedAt(){ return updatedAt; }
}
