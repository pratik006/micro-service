package com.prapps.retail.management.messaging;

import com.prapps.retail.management.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class MessagingListener {
    private final Logger LOGGER = LoggerFactory.getLogger(MessagingListener.class);

    @KafkaListener(id = "notificationListener", topics = { "placedOrders" })
    public void listen(@Payload Order order, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        System.out.println("Received: " + order + " from " + topic);
    }
}
