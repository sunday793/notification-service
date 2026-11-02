package com.example.notification_service.service;

import com.example.notification_service.model.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    // Tells Spring to listen to 'notification-topic' within the 'notification-group'
    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationEvent event) {
        System.out.println("=========================================");
        System.out.println("Consumer: Received a new event!");
        System.out.println("Event ID: " + event.getEventId());
        System.out.println("Recipient: " + event.getRecipient());
        System.out.println("Message: " + event.getMessage());

        // In a real-world scenario, the email sending logic would go here
        System.out.println("Consumer: Notification successfully 'sent'!");
        System.out.println("=========================================");
    }
}