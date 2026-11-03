package com.example.notification_service.service;

import com.example.notification_service.model.NotificationEvent;
import com.example.notification_service.repository.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private ProcessedEventRepository repository;

    @InjectMocks
    private NotificationConsumer consumer;

    @Test
    void shouldSkipDuplicateEvent() {
        // 1. Arrange: Prepare a duplicate event
        NotificationEvent event = new NotificationEvent();
        event.setEventId("123");
        event.setRecipient("test@example.com");
        event.setMessage("Hello");

        // Tell the mock repository that this event ID already exists
        when(repository.existsById("123")).thenReturn(true);

        // 2. Act: Call the consume method
        consumer.consume(event);

        // 3. Assert: Verify that the message was NOT saved again
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenMessageContainsFail() {
        // 1. Arrange
        NotificationEvent event = new NotificationEvent();
        event.setEventId("456");
        event.setMessage("This will fail");

        // The event is NOT in the DB
        when(repository.existsById("456")).thenReturn(false);

        // 2. Act & Assert: Expect a RuntimeException to be thrown
        assertThrows(RuntimeException.class, () -> consumer.consume(event));

        // Verify it didn't try to save
        verify(repository, never()).save(any());
    }

    @Test
    void shouldProcessAndSaveNewEvent() {
        // 1. Arrange
        NotificationEvent event = new NotificationEvent();
        event.setEventId("789");
        event.setMessage("Success");

        when(repository.existsById("789")).thenReturn(false);

        // 2. Act
        consumer.consume(event);

        // 3. Assert: Verify the event was saved exactly once
        verify(repository, times(1)).save(argThat(processedEvent ->
                processedEvent.getEventId().equals("789")
        ));
    }
}