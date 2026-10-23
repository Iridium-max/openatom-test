package com.example.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import java.time.Clock;

@EnableCaching
@SpringBootApplication
public class EventApplication {
    public static void main(String[] args) { SpringApplication.run(EventApplication.class, args); }
    @Bean Clock clock() { return Clock.systemUTC(); }
}
