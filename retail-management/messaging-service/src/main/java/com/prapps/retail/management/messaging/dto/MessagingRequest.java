package com.prapps.retail.management.messaging.dto;

import com.prapps.retail.management.order.Order;
import lombok.Data;

@Data
public class MessagingRequest {
    private Order payload;
}
