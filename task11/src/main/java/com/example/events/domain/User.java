package com.example.events.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="app_users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false, unique=true, length=32) private String username;
    @Column(name="password_hash", nullable=false, length=100) private String passwordHash;
    @Column(nullable=false, updatable=false) private Instant createdAt;
    protected User() {}
    public User(String username, String passwordHash, Instant createdAt) {
        this.username=username; this.passwordHash=passwordHash; this.createdAt=createdAt;
    }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}
