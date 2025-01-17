package com.example.BookingService.Controller;

import com.example.BookingService.DTOs.EventrequestDto;
import com.example.BookingService.DTOs.ShowDTO;
import com.example.BookingService.Model.Event;
import com.example.BookingService.Service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Event")
@CrossOrigin("http://localhost:3000")

public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<Event> createEvent(@RequestBody EventrequestDto eventrequestDto) {

        Event event = eventService.createEvent(eventrequestDto);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/GetEvent/{eventname}")
    public ResponseEntity<Event> GetEvent(@PathVariable String eventname){
        Event event = eventService.getEvent(eventname);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/GetAllEvent")
    public ResponseEntity<List<ShowDTO>> getAllEvent() {
        List<Event> events = eventService.getallevents();
        List<ShowDTO> event= events.stream().map(ShowDTO::new).collect(Collectors.toList());

        return ResponseEntity.ok(event); // Return the list wrapped in ResponseEntity
    }

}
