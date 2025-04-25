package com.example.NotificationService.Service.Kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class NotificationProducer {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public void sendNotification(int bookingId){
        String eventJson="{\"bookingId\":} "+bookingId+",\"sentTimestamp\":\""+ LocalDateTime.now()+"\"}";

        kafkaTemplate.send("notification-topic",eventJson);
    }
}
