package com.example.BookingService.Controller;

import com.example.BookingService.DTOs.SeatDTO;
import com.example.BookingService.Model.Seat;
import com.example.BookingService.Service.SeatBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin("http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/Movie")
public class MovieController {

    @Autowired
    private SeatBookingService seatBookingService;
    @GetMapping("/Seats/{eventname}")
    public ResponseEntity<List<SeatDTO>> getAllSeats(@PathVariable String eventname) {
        Optional<List<Seat>> seatsOptional = seatBookingService.getSeats(eventname);

        if (seatsOptional.isPresent() && !seatsOptional.get().isEmpty()) {
            List<SeatDTO> seatDTOs = seatsOptional.get().stream()
                    .map(SeatDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(seatDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

}
