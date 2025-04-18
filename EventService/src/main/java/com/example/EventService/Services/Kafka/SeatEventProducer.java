package com.example.EventService.Services.Kafka;


import com.example.EventService.DTOs.SeatLockedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class SeatEventProducer {

    private final KafkaTemplate<String, SeatLockedEvent> kafkaTemplate;

    @Autowired
    public SeatEventProducer(KafkaTemplate<String, SeatLockedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void lockSeat(String seatId, String userId) {
        SeatLockedEvent event = new SeatLockedEvent(
                Collections.singletonList(seatId),
                userId,
                LocalDateTime.now(),
                "SEAT_LOCKED",
                true
        );

        kafkaTemplate.send("seat-lock-topic", event);
        System.out.println("Published SeatLockedEvent: " + event);
    }

    @KafkaListener(topics = "seat-booked-topic", groupId = "seat-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleSeatBooking(String message) {
        System.out.println("Received raw message on seat-booked-topic: " + message);
    }
}