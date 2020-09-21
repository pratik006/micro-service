package com.prapps.retail.management.messaging;

import com.prapps.retail.management.messaging.dto.MessagingRequest;
import com.prapps.retail.management.messaging.service.MessagingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messaging")
public class MessagingController {

    private MessagingService messagingService;

    public MessagingController(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    @GetMapping
    public String alive() {
        return "alive";
    }

    @PostMapping("/send/{topicName}")
    public void create(@PathVariable String topicName, @RequestBody MessagingRequest request) {
        messagingService.enqueue(topicName, request);
    }
}
