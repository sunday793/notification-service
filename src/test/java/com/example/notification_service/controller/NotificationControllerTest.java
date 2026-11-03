package com.example.notification_service.controller;

import com.example.notification_service.model.NotificationEvent;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Autowired
    private JsonMapper objectMapper;

    @Test
    void shouldSendNotificationAndReturnOk() throws Exception {
        // 1. Arrange: Create the request payload
        NotificationEvent event = new NotificationEvent();
        event.setRecipient("test@example.com");
        event.setMessage("Hello from Test!");

        // 2. Act & 3. Assert: Send POST request and check response
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Event successfully sent")));
    }
}