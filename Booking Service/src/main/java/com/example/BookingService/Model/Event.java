package com.example.BookingService.Model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Event extends BaseModel {
    private String eventName;
    private Date eventDateTime; // Renamed for clarity
    private String eventTypeName;
    private String location;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Seat> seats;

    private Integer availableSeats;
    private String category; // "Gold", "Silver", or "Regular"
    private String position; // "Bottom", "Middle", "Top"
    private VenueType venueType;

    // Getter and Setter for eventName
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    // Getter and Setter for eventDateTime
    public Date getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(Date eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    // Getter and Setter for eventTypeName
    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    // Getter and Setter for location
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Getter and Setter for seats
    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    // Getter and Setter for availableSeats
    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    // Getter and Setter for category
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // Getter and Setter for position
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    // Getter and Setter for venueType
    public VenueType getVenueType() {
        return venueType;
    }

    public void setVenueType(VenueType venueType) {
        this.venueType = venueType;
    }

    // Helper methods for null safety
    public void addSeat(Seat seat) {
        if (seats != null) seats.add(seat);
    }

    public void removeSeat(Seat seat) {
        if (seats != null) seats.remove(seat);
    }
}
