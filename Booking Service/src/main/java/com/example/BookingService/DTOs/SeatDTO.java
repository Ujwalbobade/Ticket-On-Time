package com.example.BookingService.DTOs;

import com.example.BookingService.Model.Seat;
import com.example.BookingService.Model.SeatType;
import lombok.Data;

@Data
public class SeatDTO {
    private Long id;
    private String seatNumber;
    private SeatType seatType;
    private boolean isAvailable;
    private double price;
    private String section;
    private Long eventId;

    // Constructors, Getters, and Setters
    public SeatDTO(Seat seat) {
        this.id = seat.getId();
        this.seatNumber = seat.getSeatNumber();
        this.seatType = seat.getSeatType();
        this.isAvailable = seat.getIsAvailable();
        this.price = seat.getPrice();
        this.section = seat.getSection();
        this.eventId = seat.getEvent().getId();
    }
}
