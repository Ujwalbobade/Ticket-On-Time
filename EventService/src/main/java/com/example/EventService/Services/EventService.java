package com.example.EventService.Services;

import com.example.EventService.DTOs.BookingRequestDto;
import com.example.EventService.DTOs.EventrequestDto;
import com.example.EventService.Model.Event;
import com.example.EventService.Model.VenueType;
import com.example.EventService.Repositories.EventRepository;
import com.example.EventService.Repositories.SeatRepository;
import com.example.EventService.Strategy.SeatArrangementStrategy;
import com.example.EventService.Strategy.SeatArrangementStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        if (eventrequestDto.getEventTypeName().toUpperCase().equals("TRAIN")){
            event.setVenueType(null);
        }else{
            event.setVenueType(VenueType.valueOf(eventrequestDto.getVenueType()));}
        event.setEventStartTime(eventrequestDto.getEventStartTime());
        event.setEventEndTime(eventrequestDto.getEventEndTime());
        event.setCompartment(eventrequestDto.getCompartment());
        event.setEventUniqueID(String.valueOf(eventrequestDto.getEventuniqueid()));

        // Step 2: Save the event first to generate an ID
        event = eventRepository.save(event);
        log.info("Event created with ID: " + event.getId() + ", Name: " + event.getEventName());

        // Step 3: Get the appropriate seat arrangement strategy
        SeatArrangementStrategy strategy = seatArrangementStrategyFactory.getStrategy(eventrequestDto.getEventTypeName());

        // Step 4: Arrange and persist seats
        try {
            strategy.arrangeSeats(event);
        } catch (Exception e) {
            log.error("Error arranging seats for event: " + event.getEventName(), e);
            throw new RuntimeException("Failed to arrange seats for event: " + e.getMessage(), e);
        }

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

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean isEventValid(Long eventId) {
        String cacheKey = "event:" + eventId + ":valid";
        String cachedStatus = redisTemplate.opsForValue().get(cacheKey);

        if (cachedStatus != null) {
            return "true".equalsIgnoreCase(cachedStatus);
        }

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime eventEndTime = eventOptional.get().getEventEndTime()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            boolean isValid =  now.isBefore(eventEndTime);

            // Cache the event validity status for 10 minutes
            redisTemplate.opsForValue().set(cacheKey, String.valueOf(isValid), Duration.ofMinutes(10));

            return isValid;
        }

        return false; // Event not found or expired
    }

}