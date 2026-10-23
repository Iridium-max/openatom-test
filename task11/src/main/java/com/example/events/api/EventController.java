package com.example.events.api;

import com.example.events.api.Contracts.*;
import com.example.events.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController @RequestMapping("/api/events")
public class EventController {
    private final EventService events;
    public EventController(EventService events){ this.events=events; }
    @GetMapping
    ApiResponse<PageView<EventView>> list(@RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue="9") int size, @RequestParam(required=false) String keyword,
        @RequestParam(required=false) String status){ return ApiResponse.ok(events.list(page,size,keyword,status)); }
    @GetMapping("/{id}")
    ApiResponse<EventView> detail(@PathVariable Long id){ return ApiResponse.ok(events.detail(id)); }
    @PostMapping
    ResponseEntity<ApiResponse<EventView>> create(@Valid @RequestBody EventInput input,Principal principal){
        return ResponseEntity.status(201).body(ApiResponse.created(events.create(input,principal.getName())));
    }
    @PutMapping("/{id}")
    ApiResponse<EventView> update(@PathVariable Long id,@Valid @RequestBody EventInput input,Principal principal){
        return ApiResponse.ok(events.update(id,input,principal.getName()));
    }
    @DeleteMapping("/{id}")
    ApiResponse<Void> cancel(@PathVariable Long id,Principal principal){ events.cancel(id,principal.getName()); return ApiResponse.ok(null); }
    @PostMapping("/{id}/registrations")
    ResponseEntity<ApiResponse<RegistrationView>> register(@PathVariable Long id,Principal principal){
        return ResponseEntity.status(201).body(ApiResponse.created(events.register(id,principal.getName())));
    }
}
