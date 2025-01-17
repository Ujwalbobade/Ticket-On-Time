package com.example.BookingService.Controller;

import com.example.BookingService.DTOs.BookingRequestDto;
import com.example.BookingService.DTOs.BookingResponseDto;
import com.example.BookingService.DTOs.SeatDTO;
import com.example.BookingService.Model.Seat;
import com.example.BookingService.Service.EventService;
import com.example.BookingService.Service.SeatBookingProducer;
import com.example.BookingService.Service.SeatBookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/Bookings")
@CrossOrigin("http://localhost:3000")
@Slf4j
public class BookingController {

    @Autowired
    private SeatBookingService seatBookingService;

    @Autowired
    private EventService eventService; // Service to handle event checking

    @Autowired
    private SeatBookingProducer seatBookingProducer;

    // Booking multiple seats
    @PostMapping("/book")
    public ResponseEntity<BookingResponseDto> bookSeats(@RequestBody BookingRequestDto bookingRequest) {
        System.out.println("Received booking request: " + bookingRequest);
        // Validate the booking request
        if (bookingRequest == null || bookingRequest.getSeatno() == null || bookingRequest.getSeatno().isEmpty()
                || bookingRequest.getEventname() == null) {
            BookingResponseDto errorResponse = new BookingResponseDto(false, 0L, "Invalid booking request", null, 0D);
            return ResponseEntity.badRequest().body(errorResponse);}
        try {
            // Check if the event exists
            if (!eventService.isEventExists(bookingRequest.getEventname())) {
                BookingResponseDto errorResponse = new BookingResponseDto(false, 0L,
                        "Event not found: " + bookingRequest.getEventname(), null, 0D);
                return ResponseEntity.status(404).body(errorResponse);
            }
            BookingResponseDto bookingResponse = seatBookingService.bookSeat(bookingRequest.getSeatno(), bookingRequest.getEventname());
            return ResponseEntity.ok(bookingResponse);
        } catch (Exception e) {
            // Handle unexpected exceptions gracefully
            BookingResponseDto errorResponse = new BookingResponseDto(false, 0L,
                    "An unexpected error occurred: " + e.getMessage(), null, 0D);
            return ResponseEntity.status(500).body((errorResponse));}
    }

    @DeleteMapping("/Delete")
    public ResponseEntity<String> deleteEvent(@RequestBody BookingRequestDto bookingRequestDto) {
        try {
            // Retrieve the event based on the eventName
            log.info("deleting "+bookingRequestDto.getEventname());
           return eventService.deleteEvent(bookingRequestDto);

        } catch (Exception e) {
            // Handle any errors that occur during deletion
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting event: " + e.getMessage());
        }
    }

    @PostMapping("/book/kafka")
    public String bookSeat1(@RequestBody BookingRequestDto bookingRequest ) {
        seatBookingProducer.sendBookingRequest(String.valueOf(bookingRequest.getSeatno()), String.valueOf(bookingRequest.getUserid()),bookingRequest.getEventname());  // Send the booking request to Kafka
        return "Booking request sent for seat " + bookingRequest.getSeatno() + " by user " +  String.valueOf(bookingRequest.getUserid()) ;
    }

    @GetMapping("/Seats/{eventname}")
    public ResponseEntity<List<SeatDTO>> getAllSeats(@PathVariable String eventname) {
        Optional<List<Seat>> seatsOptional = seatBookingService.getSeats(eventname);
        if (seatsOptional.isPresent() && !seatsOptional.get().isEmpty()) {
            List<SeatDTO> seatDTOs = seatsOptional.get().stream()
                    .map(SeatDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(seatDTOs);} else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());}
    }



}
