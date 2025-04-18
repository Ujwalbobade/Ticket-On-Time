package com.example.EventService.DTOs;


import com.example.EventService.Model.Seat;
import com.example.EventService.Model.SeatType;
import com.example.EventService.Model.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SeatDTO {
    @JsonProperty("id") // Match JSON field name
    private Long id;

    @JsonProperty("seatNumber")
    private String seatNumber;

    @JsonProperty("seatType")
    private String seatType;

    @JsonProperty("price")
    private double price;

    @JsonProperty("section")
    private String section;

    @JsonProperty("eventId")
    private Long eventId;

    @JsonProperty("eventType") // If this is always null, consider removing it
    private String eventType;

    @JsonProperty("available")
    private boolean available;// Added eventType

    public SeatDTO(Seat seat) {
        this.id = seat.getId();
        this.seatNumber = seat.getSeatNumber();
        this.seatType = String.valueOf(seat.getSeatType());
        this.price = seat.getPrice();
        this.section = seat.getSection();
        this.eventId = seat.getEvent().getId();
        this.eventType = String.valueOf(seat.getEventType()); // Ensure eventType exists in Seat entity
        this.available = seat.getIsAvailable();
    }
}