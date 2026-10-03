package com.backendwork.backendApp.services;

import com.backendwork.backendApp.entity.TicketMessage;
import com.backendwork.backendApp.repository.TicketMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TicketMessageService {

    @Autowired
    private TicketMessageRepository ticketMessageRepository;

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

        return ticketMessageRepository.save(ticketMessage);
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