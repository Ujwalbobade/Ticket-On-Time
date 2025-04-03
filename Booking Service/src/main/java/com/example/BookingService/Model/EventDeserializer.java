package com.example.BookingService.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class EventDeserializer implements Deserializer<Event> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Event deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Event.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing Event", e);
        }
    }
}

