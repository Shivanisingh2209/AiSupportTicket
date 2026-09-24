package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.dto.CreateTicketRequest;
import com.backendwork.backendApp.dto.UpdateTicketRequest;
import com.backendwork.backendApp.entity.Ticket;
import com.backendwork.backendApp.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<Ticket>> getAllTickets() {
        List<Ticket> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id) {
        Ticket ticket = ticketService.getTicketById(id);

        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketById(@PathVariable String id) {
        boolean deleted = ticketService.deleteTicketById(id);

        if(!deleted) {
            return ResponseEntity.notFound().build();
        }
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