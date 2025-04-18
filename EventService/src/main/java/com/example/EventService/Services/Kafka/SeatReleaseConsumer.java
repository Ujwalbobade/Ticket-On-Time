package com.example.EventService.Services.Kafka;

import com.example.EventService.DTOs.SeatReleasedEvent;
import com.example.EventService.Services.SeatBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SeatReleaseConsumer {
    @Autowired
    private SeatBookingService seatBookingService;

    @KafkaListener(topics = "seat-release-topic", groupId = "seat-release-group")
    public void handleSeatRelease(SeatReleasedEvent event) {
        try {
            System.out.println("Releasing seat: " + event.getSeatId());

            // TODO: Update the database to mark the seat as available
            seatBookingService.releaseLockedSeats1(event.getSeatId(),event.getEventname());

        } catch (Exception e) {
            System.err.println("Error processing seat release event: " + e.getMessage());
        }
    }
}