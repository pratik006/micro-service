package com.prapps.retail.management.messaging.service;

import com.prapps.retail.management.messaging.dto.MessagingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class MessagingService {
    private KafkaTemplate<Object, Object> messagingTemplate;

    @Autowired
    public MessagingService(KafkaTemplate<Object, Object> messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public ListenableFuture<SendResult<Object, Object>> enqueue(String topicName, MessagingRequest request) {
        return this.messagingTemplate.send(topicName, request.getPayload());
    }
}
