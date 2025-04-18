package com.example.EventService.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatLockedEvent {
    private List<String> seatId;

    private  String userId;

    private LocalDateTime lockedUntil;

    private String eventName;

    private boolean lock;

}
