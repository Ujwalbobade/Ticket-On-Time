package com.example.EventService.Services.Kafka;

import com.example.EventService.DTOs.SeatBookedEvent;
import com.example.EventService.DTOs.SeatLockedEvent;
import com.example.EventService.DTOs.SeatReleasedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Slf4j
public class SeatLockConsumer {

    @Autowired
    private KafkaTemplate<String, SeatReleasedEvent> kafkaTemplate;

    private final Map<List<String>, Instant> lockedSeats = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @KafkaListener(topics = "seat-lock-topic", groupId = "seat-group")
    public void handleSeatLock(@Payload SeatLockedEvent event) {
        Instant lockTime = Instant.now();
        lockedSeats.put(event.getSeatId(), lockTime);

        // Compute delay in milliseconds
        long delay = Duration.ofMinutes(5).toMillis();
        log.info("kafka is locking seat");

        // Schedule release using a Runnable and sleep-based delay
        scheduler.execute(() -> {
            try {
                Thread.sleep(delay); // Delay for 5 minutes
                if (lockedSeats.containsKey(event.getSeatId())) {
                    Instant currentTime = Instant.now();
                    if (Duration.between(lockTime, currentTime).compareTo(Duration.ofMinutes(5)) >= 0) {
                        kafkaTemplate.send("seat-release-topic", new SeatReleasedEvent(event.getSeatId(),event.getEventName()));
                        lockedSeats.remove(event.getSeatId());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted state
            }
        });
    }

    @KafkaListener(topics = "seat-booked-topic", groupId = "seat-group")
    public void handleSeatBooking(SeatBookedEvent event) {
        // If booked, remove from lockedSeats to prevent auto-release
        lockedSeats.remove(event.getSeatId());
    }
}