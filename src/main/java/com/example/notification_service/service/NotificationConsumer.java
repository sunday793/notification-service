package com.example.notification_service.service;

import com.example.notification_service.model.NotificationEvent;
import com.example.notification_service.model.ProcessedEvent;
import com.example.notification_service.repository.ProcessedEventRepository;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private final ProcessedEventRepository repository;

    public NotificationConsumer(ProcessedEventRepository repository) {
        this.repository = repository;
    }

    @RetryableTopic(
            attempts = "3", // Total attempts: 1 original + 2 retries
            backOff = @BackOff(delay = 2000, multiplier = 2.0), // Wait 2s, then 4s
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consume(NotificationEvent event) {
        System.out.println("=========================================");
        System.out.println("Consumer: Received a new event! ID: " + event.getEventId());

        // 1. IDEMPOTENCY CHECK
        if (repository.existsById(event.getEventId())) {
            System.out.println("Consumer: Duplicate event detected! Skipping...");
            System.out.println("=========================================");
            return;
        }

        // 2. SIMULATE FAILURE FOR TESTING
        // If the message contains "fail", we throw an exception to trigger the retry logic
        if (event.getMessage().contains("fail")) {
            System.out.println("Consumer: !!! Simulating a failure !!!");
            throw new RuntimeException("Simulated external service failure");
        }

        // 3. PROCESS THE EVENT (Simulate sending email)
        System.out.println("Consumer: Processing event for " + event.getRecipient());
        System.out.println("Message: " + event.getMessage());

        // 4. MARK AS PROCESSED (Save to DB)
        repository.save(new ProcessedEvent(event.getEventId()));

        System.out.println("Consumer: Notification successfully 'sent' and saved to DB!");
        System.out.println("=========================================");
    }

    // This method handles messages that failed after all retries
    @DltHandler
    public void handleDlt(NotificationEvent event) {
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("DLQ: Message permanently failed and moved to Dead Letter Queue!");
        System.err.println("DLQ: Failed Event ID: " + event.getEventId());
        System.err.println("DLQ: Recipient: " + event.getRecipient());
        System.err.println("DLQ: Message: " + event.getMessage());
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        // In a real application, you would save this to a DB table for manual review
    }
}