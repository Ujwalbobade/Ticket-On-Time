package com.example.EventService.DTOs;

import com.example.EventService.Model.Event;
import lombok.Data;

@Data
public class ShowDTO {

    private Long id;

    private  String eventName;

    private String eventDateTime;

    private String eventTypeName;

    private String location;

    private Long eventId;

    public ShowDTO(Event event) {
        this.id = event.getId();
        this.eventName=event.getEventName();
        this.eventDateTime=event.getEventStartTime()!=null?event.getEventStartTime().toString():null;
        this.eventTypeName=event.getEventTypeName();
        this.location=event.getLocation();
        this.eventId=event.getId();
    }
}
