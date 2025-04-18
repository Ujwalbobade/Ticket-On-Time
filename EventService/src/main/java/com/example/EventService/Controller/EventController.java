package com.example.EventService.Controller;
import com.example.EventService.DTOs.BookingRequestDto;
import com.example.EventService.DTOs.EventrequestDto;
import com.example.EventService.DTOs.ShowDTO;
import com.example.EventService.DTOs.validationResponse;
import com.example.EventService.Model.Event;

import com.example.EventService.Services.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Event")
@CrossOrigin("http://localhost:3000")
@Slf4j

public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private RestTemplate restTemplate;

    private static final String AUTH_URL = "http://localhost:8080/auth/validate-token";
    // Utility method to validate token and check if the user is ADMIN
    private boolean isAdmin(String authHeader) {

        validationResponse validationResponse = validateToken(authHeader);
        return validationResponse != null && "ADMIN".equalsIgnoreCase(validationResponse.getUserrole());
    }
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

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestHeader("Authorization") String authHeader, @RequestBody EventrequestDto eventrequestDto) {
        // Check if user is ADMIN
        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(403).body(null); // Forbidden if not ADMIN
        }
        log.info(eventrequestDto.toString());
        Event event = eventService.createEvent(eventrequestDto);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/GetEvent/{eventname}")
    public ResponseEntity<Event> GetEvent(@RequestHeader("Authorization") String authHeader, @PathVariable String eventname) {
        // Check if user is ADMIN
        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(403).body(null); // Forbidden if not ADMIN
        }

        Event event = eventService.getEvent(eventname);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/GetAllEvent")
    public ResponseEntity<List<ShowDTO>> getAllEvent(@RequestHeader("Authorization") String authHeader) {
        log.info(authHeader);
        // Check if user is ADMIN
        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(403).body(null); // Forbidden if not ADMIN
        }

        List<Event> events = eventService.getallevents();
        List<ShowDTO> eventDTOs = events.stream().map(ShowDTO::new).collect(Collectors.toList());

        return ResponseEntity.ok(eventDTOs); // Return the list wrapped in ResponseEntity
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

    @GetMapping("/exists/{name}")
    public Boolean CheckUserExists(@PathVariable String name){
        try{
            return eventService.isEventExists(name);
        }catch (Exception e){
            throw new NullPointerException("No Event Exists");
        }
    }

}