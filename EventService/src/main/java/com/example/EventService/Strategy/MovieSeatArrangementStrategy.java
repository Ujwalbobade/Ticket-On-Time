package com.example.EventService.Strategy;

import com.example.EventService.Model.Event;
import com.example.EventService.Model.EventType;
import com.example.EventService.Model.Seat;
import com.example.EventService.Model.SeatType;

import com.example.EventService.Repositories.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MovieSeatArrangementStrategy implements SeatArrangementStrategy {
    @Autowired
    private SeatRepository seatRepository;

    public void arrangeSeats(Event event) {
        List<Seat> seats = new ArrayList<>();
        log.info("Event sent to Seat Strategy "+event.getEventName()+"id "+event.getId());
        String a = event.getCompartment();
        /*
        Optional<Seat> existingSeat = seatRepository.findBySeatWithEventId(event.getId());
        if(existingSeat != null){
            throw new SeatException("Another Event is present with this seat number ");
        }*/
        // Create Gold Seats (25 - Bottom)
        for (int i = 1; i <= 25; i++) {
            Seat seat = new Seat("G" + i, SeatType.GOLD, true, 50.0, "Bottom",a ,EventType.MOVIE, event);
            seats.add(seat);
            seatRepository.save(seat);  // Save to the database
        }

        // Create Silver Seats (40 - Middle)
        for (int i = 1; i <= 40; i++) {
            Seat seat = new Seat("S" + i, SeatType.SILVER, true, 30.0, "Middle",a, EventType.MOVIE, event);
            seats.add(seat);
            seatRepository.save(seat);  // Save to the database
        }

        // Create Regular Seats (20 - Top)
        for (int i = 1; i <= 20; i++) {
            Seat seat = new Seat("P" + i, SeatType.PLATINUM, true, 15.0, "Top", a,EventType.MOVIE, event);
            seats.add(seat);
            seatRepository.save(seat);  // Save to the database
        }

        // Set seats to the event
        event.setSeats(seats);
        event.setAvailableSeats(seats.size()); // Update available seats

        // Print seat allocation (for demonstration)
        System.out.println("Arranging seats for Movie event: " + event.getEventName());
        seats.forEach(System.out::println);
    }
}