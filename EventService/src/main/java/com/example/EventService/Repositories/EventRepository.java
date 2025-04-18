package com.example.EventService.Repositories;

import com.example.EventService.Model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository  extends JpaRepository<Event,Long> {
    Optional<Event> findByEventName(String eventName);
}
