package com.example.BookingService.Strategy;


import com.example.BookingService.Model.Event;

public interface SeatArrangementStrategy {
    void arrangeSeats(Event event);
}