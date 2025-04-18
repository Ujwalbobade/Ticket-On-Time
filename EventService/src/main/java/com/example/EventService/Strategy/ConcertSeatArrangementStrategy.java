package com.example.EventService.Strategy;


import com.example.EventService.Model.Event;
import com.example.EventService.Model.EventType;
import com.example.EventService.Model.Seat;
import com.example.EventService.Model.SeatType;
import org.springframework.stereotype.Component;

@Component
public class ConcertSeatArrangementStrategy implements SeatArrangementStrategy {
    @Override
    public void arrangeSeats(Event event) {
        // Logic for arranging seats for a concert event
        System.out.println("Arranging seats for Concert event.");

        // Assume we can check the event type to decide the seating arrangement
        String venueType = String.valueOf(event.getVenueType()); // e.g., "Stadium", "Lawn"

        if ("Stadium".equalsIgnoreCase(venueType)) {
            // Logic for stadium-based seating arrangement
            arrangeStadiumSeats(event);
        } else if ("Lawn".equalsIgnoreCase(venueType)) {
            // Logic for lawn-based seating arrangement (likely standing room)
            arrangeLawnSeats(event);
        } else {
            System.out.println("Unknown venue type.");
        }
    }

    private void arrangeStadiumSeats(Event event) {
        // Stadium arrangement has both seating and standing areas
        System.out.println("Arranging seats in Stadium for concert event.");

        // Seating area arrangement (e.g., reserved seating)
        arrangeSeatingArea(event);

        // Standing area arrangement (general admission or free standing)
        arrangeStandingArea(event);
    }

    private void arrangeSeatingArea(Event event) {
        // Add logic to arrange seats in the seating area (e.g., rows, sections)
        System.out.println("Arranging Seating Area for concert.");

        // Example: Divide into sections or rows based on seating configuration
        for (int i = 1; i <= 100; i++) {  // Let's assume 100 seats in the seating area
            event.getSeats().add(new Seat("S" + i, SeatType.GOLD, true, 100.0, "Seating","L-Block", EventType.CONCERT, event));
        }
    }

    private void arrangeStandingArea(Event event) {
        // Standing area doesn't have assigned seats, only standing spots
        System.out.println("Arranging Standing Area for concert.");

        // Example: 200 standing spots
        for (int i = 1; i <= 200; i++) {
            event.getSeats().add(new Seat("ST" + i, SeatType.PLATINUM, true, 30.0, "Standing",
                    "Ground",  EventType.CONCERT, event));
        }
    }

    private void arrangeLawnSeats(Event event) {
        // Standing area doesn't have assigned seats, only standing spots
        System.out.println("Arranging Standing Area for concert.");

        // Divide the standing area into sections (Near Stage, Middle Buffer, Back)

        // 1. Near Stage Area
        int nearStageCount = 50;  // Example: 50 standing spots near the stage
        for (int i = 1; i <= nearStageCount; i++) {
            event.getSeats().add(new Seat("ST-NearStage-" + i, SeatType.PLATINUM, true, 50.0, "Near Stage", "I-Block", EventType.CONCERT, event));
        }

        // 2. Middle Buffer Area
        int middleBufferCount = 100;  // Example: 100 standing spots near the middle buffer
        for (int i = 1; i <= middleBufferCount; i++) {
            event.getSeats().add(new Seat("ST-MiddleBuffer-" + i, SeatType.GOLD, true, 40.0, "Middle Buffer","P-Block",  EventType.CONCERT, event));
        }

        // 3. Back Area
        int backAreaCount = 50;  // Example: 50 standing spots at the back
        for (int i = 1; i <= backAreaCount; i++) {
            event.getSeats().add(new Seat("ST-Back-" + i, SeatType.SILVER, true, 30.0, "Back", "" +
                    "D-Block", EventType.CONCERT, event));
        }
    }

}