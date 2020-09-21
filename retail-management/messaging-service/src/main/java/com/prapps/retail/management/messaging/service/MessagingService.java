package com.prapps.retail.management.messaging.service;

import com.prapps.retail.management.messaging.dto.MessagingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {
    private KafkaTemplate<Object, Object> messagingTemplate;

    @Autowired
    public MessagingService(KafkaTemplate<Object, Object> messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void enqueue(String topicName, MessagingRequest request) {
        this.messagingTemplate.send(topicName, request.getPayload());
    }
}
