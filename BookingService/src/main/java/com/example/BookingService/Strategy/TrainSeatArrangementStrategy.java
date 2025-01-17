package com.example.BookingService.Strategy;


import com.example.BookingService.Model.Event;

public class TrainSeatArrangementStrategy implements SeatArrangementStrategy {
    @Override
    public void arrangeSeats(Event event) {
        // Logic for arranging seats for a train event (e.g., compartments, berths)
        System.out.println("Arranging seats for Train event.");
        // Seat allocation logic (compartments, window/aisle seats, etc.)
    }



}
