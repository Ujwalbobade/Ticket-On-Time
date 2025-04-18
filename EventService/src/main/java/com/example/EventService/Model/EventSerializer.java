package com.example.EventService.Model;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventSerializer implements Serializer<Event> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // Optional configuration logic if needed
    }

    @Override
    public byte[] serialize(String topic, Event data) {
        try {
            if (data == null) {
                return null;
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Event for topic: " + topic, e);
        }
    }

    @Override
    public void close() {
        // Optional: Resource cleanup
    }
}