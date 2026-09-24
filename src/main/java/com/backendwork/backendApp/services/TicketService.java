package com.backendwork.backendApp.services;

import com.backendwork.backendApp.entity.Ticket;
import com.backendwork.backendApp.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket createTicket(Ticket ticket) {
        if (ticket.getStatus() == null) {
            ticket.setStatus("OPEN");
        }

        if (ticket.getPriority() == null) {
            ticket.setPriority("MEDIUM");
        }

        if (ticket.getCreatedAt() == null) {
            ticket.setCreatedAt(new java.util.Date());
        }

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(String id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public boolean deleteTicketById(String id) {

        if (!ticketRepository.existsById(id)) {
            return false;
        }

        ticketRepository.deleteById(id);
        return true;
    }

    public Ticket updateTicket(String id, Ticket updatedTicket) {

        Ticket existingTicket = ticketRepository.findById(id).orElse(null);

        if (existingTicket == null) {
            return null;
        }

        if (updatedTicket.getCustomerName() != null) {
            existingTicket.setCustomerName(updatedTicket.getCustomerName());
        }

        if (updatedTicket.getCustomerEmail() != null) {
            existingTicket.setCustomerEmail(updatedTicket.getCustomerEmail());
        }

        if (updatedTicket.getSubject() != null) {
            existingTicket.setSubject(updatedTicket.getSubject());
        }

        if (updatedTicket.getDescription() != null) {
            existingTicket.setDescription(updatedTicket.getDescription());
        }

        if (updatedTicket.getStatus() != null) {
            existingTicket.setStatus(updatedTicket.getStatus());
        }

        if (updatedTicket.getPriority() != null) {
            existingTicket.setPriority(updatedTicket.getPriority());
        }

        return ticketRepository.save(existingTicket);
    }
}