package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.dto.*;
import com.backendwork.backendApp.entity.Ticket;
import com.backendwork.backendApp.entity.User;
import com.backendwork.backendApp.services.TicketService;
import com.backendwork.backendApp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request
    ) {

        Ticket ticket = new Ticket();

        ticket.setCustomerId(request.getCustomerId());
        ticket.setAgentId(request.getAgentId());
        ticket.setCustomerName(request.getCustomerName());
        ticket.setCustomerEmail(request.getCustomerEmail());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setSubject(request.getSubject());

        Ticket createdTicket = ticketService.createTicket(ticket);

        TicketResponse response =
                TicketMapper.toResponse(createdTicket);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getAllTickets(Pageable pageable) {
        Page<TicketResponse> tickets = ticketService.getTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ticket>> searchTickets(@RequestParam String customerEmail) {

        List<Ticket> tickets = ticketService.searchByCustomerEmail(customerEmail);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status")
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@RequestParam String status) {
        List<Ticket> tickets = ticketService.getTicketByStatus(status);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/priority")
    public ResponseEntity<List<Ticket>> getTicketsByPriority(
            @RequestParam String priority
    ) {

        List<Ticket> tickets =
                ticketService.getTicketsByPriority(priority);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/my")
    public ResponseEntity<List<TicketResponse>> getMyTickets() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        System.out.println("========== MY TICKETS ==========");
        System.out.println("Logged in email: " + email);

        User user = userService.getUserByEmail(email);

        System.out.println("User ID: " + user.getId());

        List<Ticket> tickets =
                ticketService.getMyTickets(user.getId());

        System.out.println("Tickets found: " + tickets.size());

        for (Ticket ticket : tickets) {
            System.out.println(
                    "Ticket ID: " + ticket.getId()
                            + " | Ticket Customer ID: "
                            + ticket.getCustomerId()
            );
        }

        List<TicketResponse> response =
                tickets.stream()
                        .map(TicketMapper::toResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable String id
    ) {

        Ticket ticket = ticketService.getTicketById(id);

        TicketResponse response =
                TicketMapper.toResponse(ticket);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable String id) {
        ticketService.deleteTicketById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable String id,
            @RequestBody UpdateTicketRequest request
    ) {
        Ticket ticket = new Ticket();

        ticket.setCustomerId(request.getCustomerId());
        ticket.setCustomerName(request.getCustomerName());
        ticket.setCustomerEmail(request.getCustomerEmail());
        ticket.setAgentId(request.getAgentId());
        ticket.setStatus(request.getStatus());
        ticket.setSubject(request.getSubject());
        ticket.setPriority(request.getPriority());
        ticket.setDescription(request.getDescription());

        Ticket updatedTicket = ticketService.updateTicket(id, ticket);

        if (updatedTicket == null) {
            return ResponseEntity.notFound().build();
        }
        TicketResponse response =
                TicketMapper.toResponse(updatedTicket);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Ticket>> getTicketsByAgent(
            @PathVariable String agentId
    ) {
        return ResponseEntity.ok(
                ticketService.getTicketsByAgent(agentId)
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable String id,
            @RequestBody UpdateTicketStatusRequest request
    ) {

        Ticket updatedTicket =
                ticketService.updateStatus(
                        id,
                        request.getStatus()
                );

        TicketResponse response =
                TicketMapper.toResponse(updatedTicket);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<TicketResponse> closeTicket(@PathVariable String id) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String customerEmail = authentication.getName();

        Ticket closedTicket =
                ticketService.closeTicketByCustomer(
                        id,
                        customerEmail
                );

        TicketResponse response = TicketMapper.toResponse(closedTicket);

        return ResponseEntity.ok(response);
    }
}