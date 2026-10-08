package com.backendwork.backendApp.consumer;

import com.backendwork.backendApp.events.TicketEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TicketEventConsumer {

    @KafkaListener(
            topics = "ticket-events",
            groupId = "ticket-service-group"
    )
    public void consumeTicketEvent(TicketEvent event) {

        System.out.println("=================================");
        System.out.println("Kafka Event Received");
        System.out.println("Ticket ID: " + event.getTicketId());
        System.out.println("Customer ID: " + event.getCustomerId());
        System.out.println("Customer Email: " + event.getCustomerEmail());
        System.out.println("Event Type: " + event.getEventType());
        System.out.println("Message: " + event.getMessage());
        System.out.println("=================================");
    }
}