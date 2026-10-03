package com.backendwork.backendApp.controller;

import com.backendwork.backendApp.dto.CreateTicketMessageRequest;
import com.backendwork.backendApp.entity.Agent;
import com.backendwork.backendApp.entity.Ticket;
import com.backendwork.backendApp.entity.TicketMessage;
import com.backendwork.backendApp.entity.User;
import com.backendwork.backendApp.services.AgentService;
import com.backendwork.backendApp.services.TicketMessageService;
import com.backendwork.backendApp.services.TicketService;
import com.backendwork.backendApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketMessageController {

    @Autowired
    private TicketMessageService ticketMessageService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private UserService userService;


    @PostMapping("/{ticketId}/messages")
    public ResponseEntity<TicketMessage> sendMessage(
            @PathVariable String ticketId,
            @RequestBody CreateTicketMessageRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        Ticket ticket = ticketService.getTicketById(ticketId);


        boolean isAgent =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_AGENT")
                        );

        if (isAgent) {

            Agent agent = agentService.getAgentByEmail(email);

            System.out.println("Logged in as AGENT");
            System.out.println("Agent ID: " + agent.getId());
            System.out.println("Ticket Agent ID: " + ticket.getAgentId());

            if (ticket.getAgentId() == null) {

                throw new AccessDeniedException(
                        "Ticket is not assigned to any agent"
                );
            }

            if (!ticket.getAgentId().equals(agent.getId())) {

                throw new AccessDeniedException(
                        "You are not authorized to reply to this ticket"
                );
            }

            TicketMessage message =
                    ticketMessageService.createMessage(
                            ticketId,
                            agent.getId(),
                            "AGENT",
                            request.getMessage()
                    );

            if ("OPEN".equalsIgnoreCase(ticket.getStatus())) {
                ticket.setStatus("IN_PROGRESS");
                ticketService.saveTicket(ticket);
            }

            return ResponseEntity.ok(message);
        }

        User user = userService.getUserByEmail(email);

        if (ticket.getCustomerId() == null) {

            throw new AccessDeniedException(
                    "Ticket has no customer assigned"
            );
        }

        if (!ticket.getCustomerId().equals(user.getId())) {

            throw new AccessDeniedException(
                    "You are not authorized to reply to this ticket"
            );
        }

        TicketMessage message =
                ticketMessageService.createMessage(
                        ticketId,
                        user.getId(),
                        "CUSTOMER",
                        request.getMessage()
                );

        return ResponseEntity.ok(message);
    }

    @GetMapping("/{ticketId}/messages")
    public ResponseEntity<List<TicketMessage>> getMessages(
            @PathVariable String ticketId
    ) {

        // Make sure ticket exists
        ticketService.getTicketById(ticketId);

        List<TicketMessage> messages =
                ticketMessageService
                        .getMessagesByTicketId(ticketId);

        return ResponseEntity.ok(messages);
    }

    @PutMapping("/{ticketId}/messages/{messageId}")
    public ResponseEntity<TicketMessage> updateMessage(
            @PathVariable String ticketId,
            @PathVariable String messageId,
            @RequestBody CreateTicketMessageRequest request
    ) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        TicketMessage message =
                ticketMessageService.getMessageById(messageId);

        boolean isAgent =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_AGENT")
                        );

        if (!isAgent) {
            throw new AccessDeniedException(
                    "Only agents can edit messages"
            );
        }

        Agent agent = agentService.getAgentByEmail(email);

        if (!agent.getId().equals(message.getSenderId())) {
            throw new AccessDeniedException(
                    "You can only edit your own messages"
            );
        }

        message.setMessage(request.getMessage());

        TicketMessage updatedMessage =
                ticketMessageService.saveMessage(message);

        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{ticketId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable String ticketId,
            @PathVariable String messageId
    ) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        TicketMessage message =
                ticketMessageService.getMessageById(messageId);

        boolean isAgent =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority.getAuthority()
                                                .equals("ROLE_AGENT")
                        );

        if (!isAgent) {
            throw new AccessDeniedException(
                    "Only agents can delete messages"
            );
        }

        Agent agent = agentService.getAgentByEmail(email);

        if (!agent.getId().equals(message.getSenderId())) {
            throw new AccessDeniedException(
                    "You can only delete your own messages"
            );
        }

        ticketMessageService.deleteMessage(messageId);

        return ResponseEntity.noContent().build();
    }
}