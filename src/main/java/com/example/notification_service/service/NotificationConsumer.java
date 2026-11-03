package com.example.notification_service.service;

import com.example.notification_service.model.NotificationEvent;
import com.example.notification_service.model.ProcessedEvent;
import com.example.notification_service.repository.ProcessedEventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private final ProcessedEventRepository repository;

    // Inject repository
    public NotificationConsumer(ProcessedEventRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationEvent event) {
        System.out.println("=========================================");
        System.out.println("Consumer: Received a new event! ID: " + event.getEventId());

        if (repository.existsById(event.getEventId())) {
            System.out.println("Consumer: Duplicate event detected! Skipping...");
            System.out.println("=========================================");
            return;
        }

        System.out.println("Consumer: Processing event for " + event.getRecipient());
        System.out.println("Message: " + event.getMessage());

        repository.save(new ProcessedEvent(event.getEventId()));

        System.out.println("Consumer: Notification successfully 'sent' and saved to DB!");
        System.out.println("=========================================");
    }
}