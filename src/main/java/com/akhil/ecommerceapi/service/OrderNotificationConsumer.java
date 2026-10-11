package com.akhil.ecommerceapi.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.akhil.ecommerceapi.event.OrderPlacedEvent;

@Component
public class OrderNotificationConsumer {

    private final EmailService emailService;

    public OrderNotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "order-events", groupId = "ecommerce-api-group")
    public void consume(OrderPlacedEvent event) {
        emailService.sendOrderConfirmation(event);
    }
}