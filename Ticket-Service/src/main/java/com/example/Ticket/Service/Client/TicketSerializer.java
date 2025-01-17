package com.example.Ticket.Service.Client;



import com.example.Ticket.Service.MongoModel.Ticket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class TicketSerializer implements Serializer<Ticket> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Ticket data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Ticket", e);
        }
    }

    @Override
    public void close() {
    }
}
