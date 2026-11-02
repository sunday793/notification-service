package com.example.notification_service.controller;

import com.example.notification_service.model.NotificationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private static final String TOPIC = "notification-topic";

    // Spring automatically injects KafkaTemplate based on our application.properties
    public NotificationController(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public String sendNotification(@RequestBody NotificationEvent event) {
        // Generate eventId if the client didn't provide one
        if (event.getEventId() == null || event.getEventId().isEmpty()) {
            event.setEventId(UUID.randomUUID().toString());
        }

        // Send the event to the Kafka topic
        // First arg: topic name, second arg: the message payload
        kafkaTemplate.send(TOPIC, event);

        System.out.println("API: Sent event to Kafka with ID: " + event.getEventId());

        return "Event successfully sent to the queue with ID: " + event.getEventId();
    }
}