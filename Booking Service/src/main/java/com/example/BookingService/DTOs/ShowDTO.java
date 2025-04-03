package com.example.BookingService.DTOs;

import com.example.BookingService.Model.Event;
import com.example.BookingService.Model.Seat;
import com.example.BookingService.Model.SeatType;
import lombok.Data;

@Data
public class ShowDTO {

        private Long id;
        private String eventName;
        private String eventDateTime;
        private String eventTypeName;
        private String location;
        private Long eventId;

        // Constructors
        public ShowDTO(Event event) {
            this.id = event.getId();
            this.eventName = event.getEventName();
            this.eventDateTime = event.getEventDateTime() != null ? event.getEventDateTime().toString() : null;
            this.eventTypeName = event.getEventTypeName();
            this.location = event.getLocation();
            this.eventId = event.getId(); // Assuming event.getId() returns the same ID
        }
}
