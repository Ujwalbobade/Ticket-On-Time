package com.example.EventService.Controller;


import com.example.EventService.DTOs.SeatDTO;
import com.example.EventService.DTOs.validationResponse;
import com.example.EventService.Model.Seat;
import com.example.EventService.Services.SeatBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    private RestTemplate restTemplate;

    private static final String AUTH_URL = "http://localhost:8080/auth/validate-token";  // URL to validate token

    // Utility method to validate token and check if the user is ADMIN
    private boolean isAdmin(String authHeader) {
        validationResponse validationResponse = validateToken(authHeader);
        return validationResponse != null && "ADMIN".equalsIgnoreCase(validationResponse.getUserrole());
    }

    // Utility method to validate the token
    private validationResponse validateToken(String authHeader) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<validationResponse> response = restTemplate.exchange(
                    AUTH_URL,
                    HttpMethod.GET,
                    entity,
                    validationResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid Authorization Token");
        }
    }

    @GetMapping("/Seats/{eventname}")
    public ResponseEntity<List<SeatDTO>> getAllSeats(@RequestHeader("Authorization") String authHeader, @PathVariable String eventname) {
        // Check if user is ADMIN
        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(403).body(Collections.emptyList()); // Forbidden if not ADMIN
        }

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