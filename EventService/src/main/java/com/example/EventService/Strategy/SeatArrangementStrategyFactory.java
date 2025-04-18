package com.example.EventService.Strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SeatArrangementStrategyFactory {

    @Autowired
    private MovieSeatArrangementStrategy movieSeatArrangementStrategy;

    @Autowired
    private ConcertSeatArrangementStrategy concertSeatArrangementStrategy;
    @Autowired
    private TrainSeatArrangementStrategy trainSeatArrangementStrategy;

    public SeatArrangementStrategy getStrategy(String eventType) {
        if ("Movie".equalsIgnoreCase(eventType)) {
            return movieSeatArrangementStrategy;
        } else if ("Concert".equalsIgnoreCase(eventType)) {
            return concertSeatArrangementStrategy;
        } else if ("Train".equalsIgnoreCase(eventType)) {
            return trainSeatArrangementStrategy;
        } else {
            throw new IllegalArgumentException("Unknown event type: " + eventType);
        }
    }
}