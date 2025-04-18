package com.example.EventService.Controller;
import com.example.EventService.DTOs.BookingRequestDto;
import com.example.EventService.DTOs.BookingResponseDto;
import com.example.EventService.DTOs.SeatDTO;
import com.example.EventService.DTOs.validationResponse;
import com.example.EventService.Model.Seat;
import com.example.EventService.Model.SeatStatus;

import com.example.EventService.Services.SeatBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/Seat")
public class SeatController {
    @Autowired
    private SeatBookingService seatBookingService;
    @Autowired
    private RestTemplate restTemplate;

    private static final String AUTH_URL = "http://localhost:8080/auth/validate-token";
    private validationResponse validateToken(String authHeader) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            log.info("Resposne from userservice"+entity.toString());

            ResponseEntity<validationResponse> response = restTemplate.exchange(
                    AUTH_URL,
                    HttpMethod.GET,
                    entity,
                    validationResponse.class
            );
            log.info("Resposne from userservice"+response.toString());

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid Authorization Token");
        }
    }

    @GetMapping("/checkstatus")
    public ResponseEntity<?> checkSeatStatus(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String seatno,
            @RequestParam String eventname) {
        log.info("Seat Number: {}", seatno);
        log.info("Event Name: {}", eventname);
        log.info("TOKEN"+authHeader);

        // Validate the Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid Authorization header format");
        }

        try {
            // Validate the token
            validateToken(authHeader);

            log.info("Token is valid moving ahead");

            // Fetch seat status
            SeatStatus seatStatus = seatBookingService.seatStatus(seatno, eventname);

            // Return the seat status
            return ResponseEntity.ok(seatStatus);
        } catch (RuntimeException e) {
            log.error("Error occurred while validating the token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Authorization Token");
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @GetMapping("/seats/{name}")
    public List<SeatDTO> getSeatsByEvent(@PathVariable String name) {
        Optional<List<Seat>> seats = seatBookingService.getSeats(name);
        return convertToSeatDTOList(seats.get());
    }

    // Separate function to convert List<Seat> to List<SeatDTO>
    private List<SeatDTO> convertToSeatDTOList(List<Seat> seats) {
        return seats.stream()
                .map(this::convertToSeatDTO) // Convert each seat
                .collect(Collectors.toList());
    }

    // Helper function to manually map each field
    private SeatDTO convertToSeatDTO(Seat seat) {
        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setId(seat.getId());
        seatDTO.setSeatNumber(seat.getSeatNumber());
        seatDTO.setSeatType(String.valueOf(seat.getSeatType()));
        seatDTO.setAvailable(seat.getIsAvailable()); // Adjusted for Boolean
        seatDTO.setPrice(seat.getPrice());
        seatDTO.setSection(seat.getSection());
        seatDTO.setEventId(seat.getEvent().getId());
        return seatDTO;
    }

    @PostMapping("/book/Seat")
    private BookingResponseDto bookSeat(
            @RequestHeader("Authorization") String authHeader,  // Authorization header for security
            @RequestBody BookingRequestDto bookRequest  // Request body containing booking details
    ) {
        log.info("Seat booking started"+bookRequest);
        // Validate or process the authorization header if needed
        if (authHeader == null || !validateToken(authHeader).isTokenvalidation()) {
            throw new IllegalArgumentException("Invalid or missing authorization token");
        }
        BookingResponseDto a= seatBookingService.bookSeat(
                bookRequest.getSeatno(),  // List of seats to be booked
                bookRequest.getEventname()  // Event name for the booking
        );

        // Call the service layer to book the seat(s)
        return a;
    }

    @PostMapping("/release-lock")
    public ResponseEntity<String> releaseSeatLock(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody BookingRequestDto bookingRequestDto) {

        try {
            validateToken(authHeader);
            boolean released = seatBookingService.releaseLockedSeats(bookingRequestDto.getSeatno(),bookingRequestDto.getEventname());
            if (released) {
                return ResponseEntity.ok("Seat lock released successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to release seat lock.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error releasing seat lock: " + e.getMessage());
        }
    }

}