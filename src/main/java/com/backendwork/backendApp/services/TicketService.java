package com.backendwork.backendApp.services;

import com.backendwork.backendApp.dto.TicketMapper;
import com.backendwork.backendApp.dto.TicketResponse;
import com.backendwork.backendApp.entity.Ticket;
import com.backendwork.backendApp.entity.TicketStatus;
import com.backendwork.backendApp.entity.User;
import com.backendwork.backendApp.exception.AgentNotFoundException;
import com.backendwork.backendApp.exception.CustomerNotFoundException;
import com.backendwork.backendApp.exception.ResourceNotFoundException;
import com.backendwork.backendApp.exception.TicketNotFoundException;
import com.backendwork.backendApp.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backendwork.backendApp.entity.Agent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private UserService userService;

    public Page<TicketResponse> getTickets(Pageable pageable) {
        return ticketRepository
                .findAll(pageable)
                .map(TicketMapper::toResponse);
    }

    public List<Ticket> getTicketsByAgent(String agentId) {
        return ticketRepository.findByAgentId(agentId);
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

        if (ticket.getCustomerEmail() != null) {

            User customer =
                    userService.getUserByEmail(
                            ticket.getCustomerEmail()
                    );

            ticket.setCustomerId(
                    customer.getId()
            );
        }

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
                        new ResourceNotFoundException(
                                "Ticket not found with id :" + id
                        ));
    }

    public List<Ticket> getMyTickets(String customerId) {

        return ticketRepository.findByCustomerId(customerId);
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

        Ticket existingTicket = ticketRepository.findById(id).orElseThrow(() ->
                new TicketNotFoundException(
                        "Ticket not found with id: " + id
                )
        );

        if (existingTicket == null) {
            return null;
        }

//        if (updatedTicket.getCustomerId() != null) {
//            existingTicket.setCustomerId(updatedTicket.getCustomerId());
//        }

        if (updatedTicket.getCustomerId() != null) {

            if (!customerService.customerExists(updatedTicket.getCustomerId())) {
                throw new CustomerNotFoundException(
                        "Customer not found with id: "
                                + updatedTicket.getCustomerId()
                );
            }

            existingTicket.setCustomerId(
                    updatedTicket.getCustomerId()
            );
        }

//        if (updatedTicket.getAgentId() != null) {
//            existingTicket.setAgentId(updatedTicket.getAgentId());
//        }

        if (updatedTicket.getAgentId() != null) {

            if (!agentService.agentExists(updatedTicket.getAgentId())) {
                throw new AgentNotFoundException(
                        "Agent not found with id: "
                                + updatedTicket.getAgentId()
                );
            }

            existingTicket.setAgentId(
                    updatedTicket.getAgentId()
            );
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

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String loggedInEmail = authentication.getName();

            Agent loggedInAgent =
                    agentService.getAgentByEmail(loggedInEmail);

            if (existingTicket.getAgentId() == null) {
                throw new IllegalStateException(
                        "Ticket is not assigned to any agent"
                );
            }

            if (!existingTicket.getAgentId().equals(loggedInAgent.getId())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "You are not authorized to update this ticket"
                );
            }

            try {
                TicketStatus.valueOf(
                        updatedTicket.getStatus().toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Invalid ticket status: "
                                + updatedTicket.getStatus()
                                + ". Allowed values: OPEN, IN_PROGRESS, RESOLVED, CLOSED"
                );
            }

            existingTicket.setStatus(
                    updatedTicket.getStatus().toUpperCase()
            );
        }

        if (updatedTicket.getPriority() != null) {
            existingTicket.setPriority(updatedTicket.getPriority());
        }

        return ticketRepository.save(existingTicket);
    }

    public Ticket updateStatus(
            String ticketId,
            String status
    ) {

        Ticket ticket = getTicketById(ticketId);

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String loggedInEmail =
                authentication.getName();

        Agent loggedInAgent =
                agentService.getAgentByEmail(loggedInEmail);

        if (ticket.getAgentId() == null) {
            throw new IllegalStateException(
                    "Ticket is not assigned to any agent"
            );
        }

        if (!ticket.getAgentId().equals(
                loggedInAgent.getId()
        )) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not authorized to update this ticket"
            );
        }

        try {
            TicketStatus.valueOf(
                    status.toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid ticket status: "
                            + status
                            + ". Allowed values: OPEN, IN_PROGRESS, RESOLVED, CLOSED"
            );
        }

        ticket.setStatus(
                status.toUpperCase()
        );

        return ticketRepository.save(ticket);
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket closeTicketByCustomer(String ticketId, String customerEmail) {

        Ticket ticket = getTicketById(ticketId);

        User customer = userService.getUserByEmail(customerEmail);

        if (ticket.getCustomerId() == null) {
            throw new IllegalStateException("Ticket has no customer assigned");
        }

        if(!ticket.getCustomerId().equals(customer.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to close this ticket");
        }

        if (!"RESOLVED".equalsIgnoreCase(ticket.getStatus())) {
            throw new IllegalStateException("Only Resolved tickets can be closed");
        }

        ticket.setStatus("CLOSED");

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getMyAssignedTickets() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String loggedInEmail = authentication.getName();

        Agent agent =
                agentService.getAgentByEmail(loggedInEmail);

        return ticketRepository.findByAgentId(agent.getId());
    }
}