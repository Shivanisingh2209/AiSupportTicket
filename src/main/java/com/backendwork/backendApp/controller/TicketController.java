package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.dto.CreateTicketRequest;
import com.backendwork.backendApp.dto.UpdateTicketRequest;
import com.backendwork.backendApp.entity.Ticket;
import com.backendwork.backendApp.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        Ticket ticket = new Ticket();

        ticket.setCustomerName(request.getCustomerName());
        ticket.setCustomerEmail(request.getCustomerEmail());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setSubject(request.getSubject());

        Ticket createdTicket = ticketService.createTicket(ticket);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTicket);
    }

    @GetMapping
    public ResponseEntity<Page<Ticket>> getAllTickets(Pageable pageable) {
        Page<Ticket> tickets = ticketService.getTickets(pageable);
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

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id) {
        Ticket ticket = ticketService.getTicketById(id);

        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable String id) {
        ticketService.deleteTicketById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(
            @PathVariable String id,
            @RequestBody UpdateTicketRequest request
    ) {
        Ticket ticket = new Ticket();

        ticket.setCustomerName(request.getCustomerName());
        ticket.setCustomerEmail(request.getCustomerEmail());
        ticket.setStatus(request.getStatus());
        ticket.setSubject(request.getSubject());
        ticket.setPriority(request.getPriority());
        ticket.setDescription(request.getDescription());

        Ticket updatedTicket = ticketService.updateTicket(id, ticket);

        if (updatedTicket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedTicket);
    }
}