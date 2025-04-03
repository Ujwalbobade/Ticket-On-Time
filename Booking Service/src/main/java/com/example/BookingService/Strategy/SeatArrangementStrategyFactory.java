package com.example.BookingService.Strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeatArrangementStrategyFactory {

    @Autowired
    private MovieSeatArrangementStrategy movieSeatArrangementStrategy;

    @Autowired
    private ConcertSeatArrangementStrategy concertSeatArrangementStrategy;

    public SeatArrangementStrategy getStrategy(String eventType) {
        if ("Movie".equalsIgnoreCase(eventType)) {
            return movieSeatArrangementStrategy;
        } else if ("Concert".equalsIgnoreCase(eventType)) {
            return concertSeatArrangementStrategy;
        } else {
            throw new IllegalArgumentException("Unknown event type: " + eventType);
        }
    }
}
