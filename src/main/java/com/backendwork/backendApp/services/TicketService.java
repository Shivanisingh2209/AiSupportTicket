package com.backendwork.backendApp.services;

import com.backendwork.backendApp.dto.TicketMapper;
import com.backendwork.backendApp.dto.TicketResponse;
import com.backendwork.backendApp.entity.Ticket;
import com.backendwork.backendApp.entity.TicketStatus;
import com.backendwork.backendApp.entity.User;
import com.backendwork.backendApp.exception.AgentNotFoundException;
import com.backendwork.backendApp.exception.CustomerNotFoundException;
import com.backendwork.backendApp.exception.TicketNotFoundException;
import com.backendwork.backendApp.producer.TicketEventProducer;
import com.backendwork.backendApp.events.TicketEvent;
import com.backendwork.backendApp.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backendwork.backendApp.entity.Agent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
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

    @Autowired
    private RedisService redisService;

    @Autowired
    private NotificationService notificationService;

    private final TicketEventProducer ticketEventProducer;

    public TicketService(TicketEventProducer ticketEventProducer) {
        this.ticketEventProducer = ticketEventProducer;
    }

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

        // Save ticket in MongoDB
        Ticket savedTicket =
                ticketRepository.save(ticket);

        // Publish Kafka event
        TicketEvent event =
                new TicketEvent(
                        savedTicket.getId(),
                        savedTicket.getCustomerId(),
                        savedTicket.getCustomerEmail(),
                        "TICKET_CREATED",
                        "A new support ticket has been created."
                );

        ticketEventProducer.sendTicketEvent(event);

        return savedTicket;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket getTicketById(String id) {

        String key = "ticket:" + id;

        String cachedTicket = redisService.get(key);

        if (cachedTicket != null) {
            try {

                com.fasterxml.jackson.databind.ObjectMapper objectMapper =
                        new com.fasterxml.jackson.databind.ObjectMapper();

                return objectMapper.readValue(
                        cachedTicket,
                        Ticket.class
                );

            } catch (Exception e) {
                redisService.delete(key);
            }
        }

        Ticket ticket = ticketRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Ticket not found with id :" + id
                        ));

        try {

            com.fasterxml.jackson.databind.ObjectMapper objectMapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();

            String ticketJson =
                    objectMapper.writeValueAsString(ticket);

            redisService.save(
                    key,
                    ticketJson,
                    10
            );

        } catch (Exception e) {

            System.out.println(
                    "Failed to cache ticket in Redis: "
                            + e.getMessage()
            );
        }

        return ticket;
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

        Ticket existingTicket =
                ticketRepository.findById(id).orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket not found with id: " + id
                        )
                );

        if (existingTicket == null) {
            return null;
        }

        if (updatedTicket.getCustomerId() != null) {

            if (!customerService.customerExists(
                    updatedTicket.getCustomerId()
            )) {

                throw new CustomerNotFoundException(
                        "Customer not found with id: "
                                + updatedTicket.getCustomerId()
                );
            }

            existingTicket.setCustomerId(
                    updatedTicket.getCustomerId()
            );
        }

        if (updatedTicket.getAgentId() != null) {

            if (!agentService.agentExists(
                    updatedTicket.getAgentId()
            )) {

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
            existingTicket.setCustomerName(
                    updatedTicket.getCustomerName()
            );
        }

        if (updatedTicket.getCustomerEmail() != null) {
            existingTicket.setCustomerEmail(
                    updatedTicket.getCustomerEmail()
            );
        }

        if (updatedTicket.getSubject() != null) {
            existingTicket.setSubject(
                    updatedTicket.getSubject()
            );
        }

        if (updatedTicket.getDescription() != null) {
            existingTicket.setDescription(
                    updatedTicket.getDescription()
            );
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

            String oldStatus = existingTicket.getStatus();

            String newStatus =
                    updatedTicket.getStatus().toUpperCase();

            existingTicket.setStatus(newStatus);

            createStatusNotification(
                    existingTicket,
                    oldStatus,
                    newStatus
            );
        }

        if (updatedTicket.getPriority() != null) {

            existingTicket.setPriority(
                    updatedTicket.getPriority()
            );
        }

        Ticket savedTicket =
                ticketRepository.save(existingTicket);

        redisService.delete(
                "ticket:" + id
        );

        return savedTicket;
    }

    // =========================================================
    // UPDATE STATUS
    // ADMIN → Can update any ticket
    // AGENT → Can update only assigned ticket
    // CUSTOMER → Cannot update status
    // =========================================================

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

        String role =
                authentication.getAuthorities()
                        .stream()
                        .map(authority ->
                                authority.getAuthority()
                        )
                        .findFirst()
                        .orElse("");

        // Validate status
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

        String oldStatus =
                ticket.getStatus();

        String newStatus =
                status.toUpperCase();

        // =====================================================
        // ADMIN
        // Admin can update ANY ticket
        // =====================================================

        if ("ROLE_ADMIN".equals(role)
                || "ADMIN".equals(role)) {

            ticket.setStatus(newStatus);

            Ticket savedTicket =
                    ticketRepository.save(ticket);

            createStatusNotification(
                    savedTicket,
                    oldStatus,
                    newStatus
            );

            redisService.delete(
                    "ticket:" + ticketId
            );

            return savedTicket;
        }

        // =====================================================
        // AGENT
        // Agent can update ONLY assigned ticket
        // =====================================================

        if ("ROLE_AGENT".equals(role)
                || "AGENT".equals(role)) {

            Agent loggedInAgent =
                    agentService.getAgentByEmail(
                            loggedInEmail
                    );

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

            ticket.setStatus(newStatus);

            Ticket savedTicket =
                    ticketRepository.save(ticket);

            createStatusNotification(
                    savedTicket,
                    oldStatus,
                    newStatus
            );

            redisService.delete(
                    "ticket:" + ticketId
            );

            return savedTicket;
        }


        throw new org.springframework.security.access.AccessDeniedException(
                "You are not authorized to update ticket status"
        );
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    private Ticket performClose(Ticket ticket) {

        String oldStatus =
                ticket.getStatus();

        ticket.setStatus("CLOSED");

        Ticket savedTicket =
                ticketRepository.save(ticket);

        createStatusNotification(
                savedTicket,
                oldStatus,
                "CLOSED"
        );

        redisService.delete(
                "ticket:" + ticket.getId()
        );

        return savedTicket;
    }

    public Ticket closeTicket(String ticketId) {

        Ticket ticket = getTicketById(ticketId);

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        String role =
                authentication.getAuthorities()
                        .stream()
                        .map(authority -> authority.getAuthority())
                        .findFirst()
                        .orElse("");

        // ==========================================
        // ADMIN
        // ==========================================

        if ("ROLE_ADMIN".equals(role)
                || "ADMIN".equals(role)) {

            return performClose(ticket);
        }

        // ==========================================
        // AGENT
        // ==========================================

        if ("ROLE_AGENT".equals(role)
                || "AGENT".equals(role)) {

            Agent agent =
                    agentService.getAgentByEmail(email);

            if (ticket.getAgentId() == null) {

                throw new IllegalStateException(
                        "Ticket is not assigned to any agent"
                );
            }

            if (!ticket.getAgentId().equals(agent.getId())) {

                throw new org.springframework.security.access.AccessDeniedException(
                        "You are not authorized to close this ticket"
                );
            }

            return performClose(ticket);
        }

        // ==========================================
        // CUSTOMER
        // ==========================================

        if ("ROLE_USER".equals(role)
                || "USER".equals(role)) {

            User customer =
                    userService.getUserByEmail(email);

            if (ticket.getCustomerId() == null) {

                throw new IllegalStateException(
                        "Ticket has no customer assigned"
                );
            }

            if (!ticket.getCustomerId()
                    .equals(customer.getId())) {

                throw new org.springframework.security.access.AccessDeniedException(
                        "You are not authorized to close this ticket"
                );
            }

            if (!"RESOLVED".equalsIgnoreCase(
                    ticket.getStatus()
            )) {

                throw new IllegalStateException(
                        "Only Resolved tickets can be closed"
                );
            }

            return performClose(ticket);
        }

        throw new org.springframework.security.access.AccessDeniedException(
                "You are not authorized to close this ticket"
        );
    }

    public List<Ticket> getMyAssignedTickets() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String loggedInEmail =
                authentication.getName();

        Agent agent =
                agentService.getAgentByEmail(
                        loggedInEmail
                );

        return ticketRepository.findByAgentId(
                agent.getId()
        );
    }

    public Map<String, Long> getTicketStatistics() {

        Map<String, Long> stats =
                new LinkedHashMap<>();

        stats.put(
                "total",
                ticketRepository.count()
        );

        stats.put(
                "open",
                ticketRepository.countByStatus("OPEN")
        );

        stats.put(
                "inProgress",
                ticketRepository.countByStatus("IN_PROGRESS")
        );

        stats.put(
                "resolved",
                ticketRepository.countByStatus("RESOLVED")
        );

        stats.put(
                "closed",
                ticketRepository.countByStatus("CLOSED")
        );

        return stats;
    }

    private void createStatusNotification(
            Ticket ticket,
            String oldStatus,
            String newStatus
    ) {

        if (ticket.getCustomerId() == null ||
                ticket.getCustomerId().isBlank()) {
            return;
        }

        if (oldStatus != null &&
                oldStatus.equalsIgnoreCase(newStatus)) {
            return;
        }

        User customer = userService
                .getUserByEmail(ticket.getCustomerEmail());

        String message;

        switch (newStatus.toUpperCase()) {

            case "IN_PROGRESS":
                message = "Your ticket is now being worked on.";
                break;

            case "RESOLVED":
                message = "Your ticket has been resolved.";
                break;

            case "CLOSED":
                message = "Your ticket has been closed.";
                break;

            case "OPEN":
                message = "Your ticket has been reopened.";
                break;

            default:
                message = "Your ticket status has been updated to "
                        + newStatus + ".";
        }

        notificationService.createNotification(
                customer.getId(),
                customer.getName(),
                message,
                "STATUS",
                ticket.getId()
        );
    }

    public Ticket autoAssignTicket(
            String ticketId
    ) {

        Ticket ticket =
                ticketRepository.findById(ticketId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Ticket not found with id: "
                                                + ticketId
                                )
                        );

        if (ticket.getAgentId() != null) {

            throw new IllegalStateException(
                    "Ticket is already assigned to an agent"
            );
        }

        Agent agent =
                agentService.getLeastLoadedAvailableAgent();

        ticket.setAgentId(
                agent.getId()
        );

        return ticketRepository.save(ticket);
    }

    public Ticket assignTicketToAgent(
            String ticketId,
            String agentId
    ) {

        Ticket ticket = getTicketById(ticketId);

        if (!agentService.agentExists(agentId)) {
            throw new AgentNotFoundException(
                    "Agent not found with id: " + agentId
            );
        }

        ticket.setAgentId(agentId);

        Ticket savedTicket =
                ticketRepository.save(ticket);

        redisService.delete(
                "ticket:" + ticketId
        );

        return savedTicket;
    }
}