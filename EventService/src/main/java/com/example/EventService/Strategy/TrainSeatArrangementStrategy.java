package com.example.EventService.Strategy;

import com.example.EventService.Model.Event;
import com.example.EventService.Model.EventType;
import com.example.EventService.Model.Seat;
import com.example.EventService.Model.SeatType;
import com.example.EventService.Repositories.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class TrainSeatArrangementStrategy implements SeatArrangementStrategy {
    private static final int SEATS_PER_COMPARTMENT = 8;  // 6 berths + 2 side seats
    private static final int SEATS_PER_COACH = 72; // Typical coach capacity

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public void arrangeSeats(Event event) {
        System.out.println("Arranging seats for Train event: " + event.getEventName());

        List<Seat> seats = new ArrayList<>();
        int totalCoaches= Integer.parseInt(event.getCompartment());

        for (int coachNumber = 1; coachNumber <= totalCoaches; coachNumber++) {
            String coachName = "B" + coachNumber; // Coach Name Format: B1, B2, B3...

            for (int seatNumber = 1; seatNumber <= SEATS_PER_COACH; seatNumber++) {
                seats.add(createSeat(coachName, seatNumber, event));
            }
        }

        // Persist seats in the database (if using JPA)
        saveSeatsToDatabase(seats);
        event.setSeats(seats);
        event.setAvailableSeats(seats.size());
    }

    private Seat createSeat(String coachName, int seatNumber, Event event) {
        String[] seatTypes = {"LB", "MB", "UB", "LB", "MB", "UB", "SLB", "SUB"};
        SeatType seatType = SeatType.valueOf(seatTypes[(seatNumber - 1) % SEATS_PER_COMPARTMENT]); // Cycle seat types

        String seatId = coachName + "-S" + seatNumber; // Format: B3-SX

        return new Seat(seatId, seatType, true, 100.0, "General", coachName, EventType.TRAIN, event);
    }

    private void saveSeatsToDatabase(List<Seat> seats) {
        // Persist seats using repository (assuming JPA is used)
        seatRepository.saveAll(seats);

        System.out.println("Seats successfully arranged and stored in DB.");
    }

}