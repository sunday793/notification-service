package com.example.notification_service.model;


public class NotificationEvent {

    private String eventId;
    private String recipient;
    private String message;

    // Default constructor is required for Jackson (JSON to Object deserialization)
    public NotificationEvent() {
    }

    public NotificationEvent(String eventId, String recipient, String message) {
        this.eventId = eventId;
        this.recipient = recipient;
        this.message = message;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
