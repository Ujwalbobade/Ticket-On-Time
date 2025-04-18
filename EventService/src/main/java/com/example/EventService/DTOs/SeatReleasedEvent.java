package com.example.EventService.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatReleasedEvent {
    @JsonProperty("seatId")
    private List<String> seatId;
    @JsonProperty("eventName")
    private String eventname;
}
