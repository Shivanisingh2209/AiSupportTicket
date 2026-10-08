package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.events.TicketEvent;
import com.backendwork.backendApp.producer.TicketEventProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaTestController {

    private final TicketEventProducer producer;

    public KafkaTestController(TicketEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/test")
    public String testKafka() {

        TicketEvent event = new TicketEvent(
                "TEST-001",
                "TEST-CUSTOMER",
                "test@example.com",
                "TEST",
                "Kafka test event"
        );

        producer.sendTicketEvent(event);

        return "Kafka event sent successfully";
    }
}
