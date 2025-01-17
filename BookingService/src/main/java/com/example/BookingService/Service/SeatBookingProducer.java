package com.example.BookingService.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeatBookingProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "seat-booking-topic"; // Kafka topic

    public void sendBookingRequest(String seatNumber, String userId,String eventName) {
        String bookingMessage = "Booking request for seat: " + seatNumber + " by user: " + userId+"For Event "+eventName;
        kafkaTemplate.send(TOPIC, bookingMessage);
    }
}
