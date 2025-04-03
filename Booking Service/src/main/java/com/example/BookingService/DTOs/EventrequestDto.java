package com.example.BookingService.DTOs;

import com.example.BookingService.Model.VenueType;

public class EventrequestDto {

    private String Eventname;
    private String EventTypeName;
    private String Location;
    private Integer availableSeats;
    private VenueType venueType;

    // Getter and Setter for Eventname
    public String getEventname() {
        return Eventname;
    }

    public void setEventname(String eventname) {
        Eventname = eventname;
    }

    // Getter and Setter for EventTypeName
    public String getEventTypeName() {
        return EventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        EventTypeName = eventTypeName;
    }

    // Getter and Setter for Location
    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    // Getter and Setter for availableSeats
    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    // Getter and Setter for venueType
    public VenueType getVenueType() {
        return venueType;
    }

    public void setVenueType(VenueType venueType) {
        this.venueType = venueType;
    }
}
