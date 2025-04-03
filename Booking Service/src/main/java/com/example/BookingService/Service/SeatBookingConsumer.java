package com.example.BookingService.Service;


import com.example.BookingService.DTOs.BookingRequestDto;
import com.example.BookingService.DTOs.BookingResponseDto;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SeatBookingConsumer {

    @Autowired
    private SeatBookingService seatBookingService;

    @KafkaListener(topics = "seat-booking-topic", groupId = "seat-booking-group")
    public void consumeBookingRequest(String message) {
        // Extract seat number and user ID using regular expressions
        String seatNumber = extractSeatNumber(message);
        Long userId = extractUserId(message);

        if (seatNumber != null && userId != null) {
            BookingRequestDto bookingRequestDto = new BookingRequestDto();
            bookingRequestDto.setUserid(userId);
            bookingRequestDto.setSeatno(Collections.singletonList(seatNumber));

            // Call the service to book the seat

            BookingResponseDto bp = seatBookingService.bookSeat(bookingRequestDto.getSeatno(), bookingRequestDto.getEventname());
            boolean bookingSuccess=bp.getStatus();

            if (bookingSuccess) {
                System.out.println("Successfully booked seat " + seatNumber + " for user " + userId);
            } else {
                System.out.println("Booking failed for seat " + seatNumber + " for user " + userId);
            }
        } else {
            System.out.println("Failed to extract seat number or user ID from the message.");
        }
    }

    private String extractSeatNumber(String message) {
        // Using a regular expression to extract any alphanumeric seat number (e.g., S12, A5, etc.)
        Pattern pattern = Pattern.compile("\\b([A-Z0-9]+\\d+)\\b"); // Pattern to match seat numbers like S12, A5, etc.
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1); // Returns the seat number
        } else {
            System.out.println("Seat number not found in the message.");
            return null;
        }
    }

    private Long extractUserId(String message) {
        // Extract user ID and ensure it is numeric
        String[] parts = message.split(" ");
        for (String part : parts) {
            if (part.startsWith("user:")) {
                try {
                    return Long.valueOf(part.replace("user:", "").trim());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid user ID format: " + part);
                    return null;
                }
            }
        }
        return null;
    }
}
