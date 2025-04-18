package com.example.EventService.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EventDeserializer {
    private final ObjectMapper objectMapper = new ObjectMapper();


    public Event deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Event.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing Event", e);
        }
    }
}
