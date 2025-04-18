package com.example.EventService.Services;
import com.example.EventService.DTOs.BookingResponseDto;
import com.example.EventService.DTOs.SeatLockedEvent;
import com.example.EventService.Model.Event;
import com.example.EventService.Model.Seat;
import com.example.EventService.Model.SeatStatus;

import com.example.EventService.Repositories.EventRepository;
import com.example.EventService.Repositories.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SeatBookingService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private EventRepository eventRepository;
    @Autowired

    private KafkaTemplate kafkaTemplate;
    @Autowired
    private EventService eventService;

    @Transactional
    public BookingResponseDto bookSeat(List<String> seatNumbers, String eventName) {
        Optional<Event> eventOptional = eventRepository.findByEventName(eventName);
        BookingResponseDto finalResponse = new BookingResponseDto();
        List<String> lockedSeats = new ArrayList<>();
        double totalPrice = 0.0;

        if (!eventOptional.isPresent()) {
            log.error("Event not found: " + eventName);
            finalResponse.setEventname(eventName);
            finalResponse.setStatus(Boolean.FALSE);
            return finalResponse;
        }

        Event event = eventOptional.get();
        log.info("Event: " + event.getEventName() + " found with ID: " + event.getId());
        if(!eventService.isEventValid(event.getId())){
            log.error("Event expired  : " + eventName);
            finalResponse.setEventname(eventName);
            finalResponse.setStatus(Boolean.FALSE);
            return finalResponse;  }
        for (String seatNumber : seatNumbers) {
            try {
                Optional<Seat> seatOptional = seatRepository.findBySeatNumberAndEventId(seatNumber, event.getId());

                if (seatOptional.isPresent()) {
                    Seat seat = seatOptional.get();
                    // finalResponse.setEvent(seat.getEventType().toString());
                    log.info("Event type "+seat.getEventType());

                    synchronized (seat) {
                        log.info("Processing seat: " + seat.getSeatNumber() + ", Availability: " + seat.getIsAvailable());

                        if (seat.getIsAvailable() && !seat.getIsLocked()) {
                            seat.setIsAvailable(false);
                            seat.setIsLocked(true);
                            seat.setLockedUntil(LocalDateTime.now().plusMinutes(5)); // Lock for 5 minutes
                            seatRepository.save(seat); // Save the updated seat

                            lockedSeats.add(seat.getSeatNumber());
                            totalPrice += seat.getPrice();

                            log.info("Seat " + seatNumber + " successfully locked.");
                        } else {
                            log.warn("Seat " + seatNumber + " is not available or already locked.");
                        }
                    }
                } else {
                    log.warn("Seat " + seatNumber + " does not exist for event: " + event.getEventName());
                }
            } catch (Exception ex) {
                log.error("Error locking seat " + seatNumber + ": " + ex.getMessage());
            }
        }

        finalResponse.setEventname(event.getEventName());
        finalResponse.setSeatnumber(String.join(", ", lockedSeats));
        finalResponse.setPrice(totalPrice);
        finalResponse.setStatus(!lockedSeats.isEmpty()); // True if at least one seat was locked
        finalResponse.setEtype(eventOptional.get().getEventTypeName());

        log.info(finalResponse.toString());

        return finalResponse;
    }

    public Optional<List<Seat>> getSeats(String eventname) {
        Optional<Event> eventid= eventRepository.findByEventName(eventname);
        log.info("Event no"+eventid.get().getId());

        return  seatRepository.findBySeatWithEventId(eventid.get().getId());
    }

    public SeatStatus seatStatus(String seatno,String eventname) {
        Optional<Event> e=eventRepository.findByEventName(eventname);
        Optional<Seat> s=seatRepository.findBySeatNumberAndEventId(seatno,e.get().getId());
        if(s.get().getIsAvailable()){
            return SeatStatus.AVAILABLE;
        }else{
            return SeatStatus.BOOKED;
        }
    }

    @Transactional
    public boolean releaseLockedSeats1(List<String> seatNumbers, String eventName) {
        Optional<Event> eventOptional = eventRepository.findByEventName(eventName);

        if (!eventOptional.isPresent()) {
            log.error("Event not found: " + eventName);
            return false;
        }

        Event event = eventOptional.get();

        for (String seatNumber : seatNumbers) {
            Optional<Seat> seatOptional = seatRepository.findBySeatNumberAndEventId(seatNumber, event.getId());

            if (seatOptional.isPresent()) {
                Seat seat = seatOptional.get();

                if (seat.getIsLocked()) {
                    seat.setIsAvailable(true);
                    seat.setIsLocked(false);
                    seat.setLockedUntil(null);
                    seatRepository.save(seat);

                    log.info("Seat " + seatNumber + " released due to payment failure.");
                    kafkaTemplate.send("seat-release-topic", seat);
                    log.info("Published seat release event for seat {}", seatNumber);
                }
            }
        }
        return true;
    }

    @Transactional
    public boolean releaseLockedSeats(List<String> seatNumbers, String eventName) {
        Optional<Event> eventOptional = eventRepository.findByEventName(eventName);

        if (!eventOptional.isPresent()) {
            log.error("Event not found: " + eventName);
            return false;
        }

        Event event = eventOptional.get();

        for (String seatNumber : seatNumbers) {
            Optional<Seat> seatOptional = seatRepository.findBySeatNumberAndEventId(seatNumber, event.getId());

            if (seatOptional.isPresent()) {
                Seat seat = seatOptional.get();

                if (seat.getIsLocked()) {
                    SeatLockedEvent releaseEvent = new SeatLockedEvent();
                    releaseEvent.setSeatId(Collections.singletonList(seatNumber));
                    releaseEvent.setEventName(eventName);
                    releaseEvent.setLock(false);

                    kafkaTemplate.send("seat-release-topic", releaseEvent);
                    log.info("Published seat release event for seat {}", seatNumber);
                }
            }
        }
        return true;
    }

    // Run a background job every 1 minute to unlock expired seats
    @Scheduled(fixedRate = 60000) // Runs every 1 minute
    @Transactional
    public void autoReleaseLockedSeats() {
        LocalDateTime now = LocalDateTime.now();

        int releasedSeats = seatRepository.unlockExpiredSeats(now);

        if (releasedSeats > 0) {
            log.info("{} seats automatically released after lock expiration.", releasedSeats);
        }
    }

}

