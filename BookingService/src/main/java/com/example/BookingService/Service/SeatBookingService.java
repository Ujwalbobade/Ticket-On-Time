package com.example.BookingService.Service;

import com.example.BookingService.DTOs.BookingResponseDto;
import com.example.BookingService.Model.Event;
import com.example.BookingService.Model.Seat;

import com.example.BookingService.Repository.EventRepository;
import com.example.BookingService.Repository.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SeatBookingService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public BookingResponseDto bookSeat(List<String> seatNumbers, String eventName) {
        Optional<Event> eventOptional = eventRepository.findByEventName(eventName);
        BookingResponseDto finalResponse = new BookingResponseDto();
        List<String> bookedSeats = new ArrayList<>();
        double totalPrice = 0.0;

        if (!eventOptional.isPresent()) {
            log.error("Event not found: " + eventName);
            finalResponse.setEventname(eventName);
            finalResponse.setStatus(Boolean.FALSE);
            return finalResponse;
        }

        Event event = eventOptional.get();
        log.info("Event: " + event.getEventName() + " found with ID: " + event.getId());

        for (String seatNumber : seatNumbers) {
            try {
                Optional<Seat> seatOptional = seatRepository.findBySeatNumberAndEventId(seatNumber, event.getId());

                if (seatOptional.isPresent()) {
                    Seat seat = seatOptional.get();

                    synchronized (seat) {
                        log.info("Processing seat: " + seat.getSeatNumber() + ", Availability: " + seat.getIsAvailable());

                        if (seat.getIsAvailable()) {
                            seat.setIsAvailable(false);
                            seatRepository.save(seat); // Save the updated seat

                            bookedSeats.add(seat.getSeatNumber());
                            totalPrice += seat.getPrice();

                            log.info("Seat " + seatNumber + " successfully booked.");
                        } else {
                            log.warn("Seat " + seatNumber + " is not available.");
                        }
                    }
                } else {
                    log.warn("Seat " + seatNumber + " does not exist for event: " + event.getEventName());
                }
            } catch (Exception ex) {
                log.error("Error booking seat " + seatNumber + ": " + ex.getMessage());
            }
        }

        finalResponse.setEventname(event.getEventName());
        finalResponse.setSeatnumber(String.join(", ", bookedSeats)); // Joining seat numbers as a single string
        finalResponse.setPrice(totalPrice);
        finalResponse.setStatus(!bookedSeats.isEmpty()); // True if at least one seat was booked
       // finalResponse.setBooking_id();

        return finalResponse;
    }

    public Optional<List<Seat>> getSeats(String eventname) {
        Optional<Event> eventid= eventRepository.findByEventName(eventname);
        log.info("Event no"+eventid.get().getId());

        return  seatRepository.findBySeatWithEventId(eventid.get().getId());
    }

/*

    @Transactional
    public BookingResponseDto bookSeat(String seatNumber, String EventName) {

        Optional<Event> e= eventRepository.findByEventName(EventName);
        BookingResponseDto bp= new BookingResponseDto();
        Optional<Seat> s = seatRepository.findBySeatNumberAndEventId(seatNumber,e.get().getId());
        log.info("Event :======"+e.get().getEventName());
        log.info("Seat :======"+s.get().getSeatNumber()+"is avaibalable===="+s.get().getIsAvailable());
        Seat seat=s.get();
        if (seat != null && seat.getIsAvailable() && e!= null) {
            log.info("Enter seat booking ");
            synchronized (seat) {
                log.info("seat booking saving processing "+seat.getEvent().getEventName().equals(EventName)+"  "+seat.getIsAvailable());
                // Locking the seat object during the booking process
                if (seat.getIsAvailable() && seat.getEvent().getEventName().equals(EventName)){
                    seat.setIsAvailable(false);
                    bp.setBooking_id(seat.getId());
                    bp.setSeatnumber(seat.getSeatNumber());
                    bp.setEventname(seat.getEvent().getEventName());
                    bp.setPrice(seat.getPrice());
                    log.info("seat booking saving processing ");
                    seatRepository.save(seat); // Save the updated seat
                    System.out.println("Seat " + seatNumber + " successfully booked.");
                    bp.setStatus(Boolean.TRUE);
                    return bp;
                } else {
                    System.out.println("Seat " + seatNumber + " is not available.");
                    bp.setStatus(Boolean.FALSE);
                    return bp;
                }
            }
        } else {
            System.out.println("Seat " + seatNumber + " does not exist.");
            bp.setStatus(Boolean.FALSE);
            return bp;
        }
    }*/


}
