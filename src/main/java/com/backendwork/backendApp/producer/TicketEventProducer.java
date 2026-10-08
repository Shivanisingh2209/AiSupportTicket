package com.backendwork.backendApp.producer;

import com.backendwork.backendApp.events.TicketEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketEventProducer {

    private static final String TOPIC = "ticket-events";

    private final KafkaTemplate<String, TicketEvent> kafkaTemplate;

    public TicketEventProducer(
            KafkaTemplate<String, TicketEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTicketEvent(TicketEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.getTicketId(),
                event
        );

        System.out.println(
                "Kafka Event Sent: "
                        + event.getEventType()
                        + " | Ticket ID: "
                        + event.getTicketId()
        );
    }
}