package com.example.BookingService.Service;


import com.example.BookingService.DTOs.BookingRequestDto;
import com.example.BookingService.DTOs.EventrequestDto;
import com.example.BookingService.Model.Event;
import com.example.BookingService.Model.Seat;
import com.example.BookingService.Repository.EventRepository;
import com.example.BookingService.Repository.SeatRepository;
import com.example.BookingService.Strategy.SeatArrangementStrategy;
import com.example.BookingService.Strategy.SeatArrangementStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EventService {

    @Autowired
    private SeatArrangementStrategyFactory seatArrangementStrategyFactory;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private SeatRepository seatRepository;

    // Create an event and trigger the appropriate seat arrangement strategy
    @Transactional
    public Event createEvent(EventrequestDto eventrequestDto) {
        // Step 1: Create the event object
        Event event = new Event();
        event.setEventName(eventrequestDto.getEventname());
        event.setAvailableSeats(eventrequestDto.getAvailableSeats());
        event.setLocation(eventrequestDto.getLocation());
        event.setEventTypeName(eventrequestDto.getEventTypeName());
        log.info("Event id" + event.getId() + " Event Name" + event.getEventName());

        // Step 2: Get the appropriate seat arrangement strategy based on the event type
        SeatArrangementStrategy strategy = seatArrangementStrategyFactory.getStrategy(eventrequestDto.getEventTypeName());

        // Step 3: Create and save the seats first (ensure the seats are persisted)
        try {
            // Call the strategy to arrange the seats and save them
            strategy.arrangeSeats(event);
        } catch (Exception e) {
            // If seat arrangement fails, log the error and the transaction will be rolled back automatically
            log.error("Error arranging seats for event: " + event.getEventName(), e);
            throw new RuntimeException("Failed to arrange seats for event.");
        }

        // Step 4: Save the event object only after the seats have been successfully arranged
        eventRepository.save(event);

        return event;
    }


    public boolean isEventExists(String eventname) {
        if(true) {
            Optional<Event> event = eventRepository.findByEventName(eventname); // Finds the event by name
            return event != null;
        }else{
            return false;
        }
    }

    public Event getEvent(String eventname) {
        Optional<Event> a = eventRepository.findByEventName(eventname);
        return a.get();
    }
    public ResponseEntity<String> deleteEvent(@RequestBody BookingRequestDto bookingRequestDto) {
        try {
            // Retrieve the event based on the eventName
            Optional<Event> eventOptional = eventRepository.findByEventName(bookingRequestDto.getEventname());

            if (!eventOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Event not found with name: " + bookingRequestDto.getEventname());
            }

            Event event = eventOptional.get();

            // Delete all related seats
            seatRepository.deleteById(event.getId());

            // Delete the event
            eventRepository.delete(event);

            return ResponseEntity.ok("Event and associated seats deleted successfully: " + bookingRequestDto.getEventname());
        } catch (Exception e) {
            // Handle any errors that occur during deletion
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting event: " + e.getMessage());
        }
    }

    public List<Event> getallevents() {
        return  eventRepository.findAll();
    }
}
