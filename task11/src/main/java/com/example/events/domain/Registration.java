package com.example.events.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="registrations", uniqueConstraints=@UniqueConstraint(name="uq_registration_user_event", columnNames={"user_id", "event_id"}))
public class Registration {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="user_id") private User user;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="event_id") private Event event;
    @Column(nullable=false, updatable=false) private Instant createdAt;
    protected Registration() {}
    public Registration(User user, Event event, Instant now){ this.user=user; this.event=event; this.createdAt=now; }
    public Long getId(){ return id; }
    public User getUser(){ return user; }
    public Event getEvent(){ return event; }
    public Instant getCreatedAt(){ return createdAt; }
}
