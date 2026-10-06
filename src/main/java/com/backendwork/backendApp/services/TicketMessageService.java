package com.backendwork.backendApp.services;

import com.backendwork.backendApp.entity.Ticket;
import com.backendwork.backendApp.entity.TicketMessage;
import com.backendwork.backendApp.entity.User;
import com.backendwork.backendApp.repository.TicketMessageRepository;
import com.backendwork.backendApp.repository.TicketRepository;
import com.backendwork.backendApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TicketMessageService {

    @Autowired
    private TicketMessageRepository ticketMessageRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public TicketMessage createMessage(
            String ticketId,
            String senderId,
            String senderRole,
            String message
    ) {

        TicketMessage ticketMessage = new TicketMessage();

        ticketMessage.setTicketId(ticketId);
        ticketMessage.setSenderId(senderId);
        ticketMessage.setSenderRole(senderRole);
        ticketMessage.setMessage(message);
        ticketMessage.setCreatedAt(new Date());

        // Save message first
        TicketMessage savedMessage =
                ticketMessageRepository.save(ticketMessage);

        /*
         * Create notification only when AGENT sends a message.
         */
        if ("AGENT".equalsIgnoreCase(senderRole)) {

            Ticket ticket = ticketRepository
                    .findById(ticketId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Ticket not found with id: " + ticketId
                            )
                    );

            String customerId = ticket.getCustomerId();

            if (customerId != null && !customerId.isBlank()) {

                User customer = userRepository
                        .findById(customerId)
                        .orElse(null);

                if (customer != null) {

                    notificationService.createNotification(
                            customer.getId(),
                            customer.getName(),
                            "Agent has replied to your ticket.",
                            "MESSAGE",
                            ticketId
                    );
                }
            }
        }

        return savedMessage;
    }

    public List<TicketMessage> getMessagesByTicketId(String ticketId) {

        return ticketMessageRepository
                .findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    public TicketMessage getMessageById(String messageId) {
        return ticketMessageRepository
                .findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Message not found"
                        ));
    }

    public TicketMessage saveMessage(TicketMessage message) {
        return ticketMessageRepository.save(message);
    }

    public void deleteMessage(String messageId) {
        ticketMessageRepository.deleteById(messageId);
    }
}