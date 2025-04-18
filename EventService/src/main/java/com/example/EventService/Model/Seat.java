package com.example.EventService.Model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "seats") // Maps to the "seats" table in MySQL
public class Seat extends BaseModel {

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "section", nullable = false)
    private String section;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false; // Default false

    @Column(name = "locked_until", nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
    private LocalDateTime lockedUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "compartment", nullable = false)
    private String compartment;
    // ✅ Updated Constructor (Added compartmentNumber)
    public Seat(String seatNumber, SeatType seatType, Boolean isAvailable, Double price,
                String section, String compartmentNumber, EventType eventType, Event event) {
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isAvailable = isAvailable;
        this.price = price;
        this.section = section;
        this.compartment = compartmentNumber;
        this.eventType = eventType;
        this.event = event;
        this.isLocked = false;  // Explicitly set default value
        this.lockedUntil = null; // Explicitly set default value
    }
}