package com.backendwork.backendApp.services;

import com.backendwork.backendApp.entity.Ticket;
import com.backendwork.backendApp.exception.AgentNotFoundException;
import com.backendwork.backendApp.exception.CustomerNotFoundException;
import com.backendwork.backendApp.exception.TicketNotFoundException;
import com.backendwork.backendApp.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AgentService agentService;

    public Page<Ticket> getTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    public List<Ticket> searchByCustomerEmail(String customerEmail) {
        return ticketRepository.findByCustomerEmail(customerEmail);
    }

    public List<Ticket> getTicketByStatus(String status) {
        return ticketRepository.findByStatus(status);
    }

    public List<Ticket> getTicketsByPriority(String priority) {
        return ticketRepository.findByPriority(priority);
    }

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
        return ticketRepository
                .findById(id)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket not found with id :" + id
                        ));
    }

    public void deleteTicketById(String id) {

        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(
                    "Ticket not found with id: " + id
            );
        }

        ticketRepository.deleteById(id);
    }

    public Ticket updateTicket(String id, Ticket updatedTicket) {

        Ticket existingTicket = ticketRepository.findById(id).orElse(null);

        if (existingTicket == null) {
            return null;
        }

        if (updatedTicket.getCustomerId() != null) {
            existingTicket.setCustomerId(updatedTicket.getCustomerId());
        }

        if (updatedTicket.getCustomerId() != null) {

            if (!customerService.customerExists(updatedTicket.getCustomerId())) {
                throw new CustomerNotFoundException(
                        "Customer not found with id: "
                                + updatedTicket.getCustomerId()
                );
            }
        }

        if (updatedTicket.getAgentId() != null) {
            existingTicket.setAgentId(updatedTicket.getAgentId());
        }

        if (updatedTicket.getAgentId() != null) {

            if (!agentService.agentExists(updatedTicket.getAgentId())) {
                throw new AgentNotFoundException(
                        "Agent not found with id: "
                                + updatedTicket.getAgentId()
                );
            }
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