package com.backendwork.backendApp.dto;
import com.backendwork.backendApp.entity.Ticket;

public class TicketMapper {

    public static TicketResponse toResponse(Ticket ticket) {

        TicketResponse response = new TicketResponse();

        response.setId(ticket.getId());
        response.setCustomerId(ticket.getCustomerId());
        response.setAgentId(ticket.getAgentId());
        response.setCustomerName(ticket.getCustomerName());
        response.setCustomerEmail(ticket.getCustomerEmail());
        response.setSubject(ticket.getSubject());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setPriority(ticket.getPriority());
        response.setCreatedAt(ticket.getCreatedAt());

        return response;
    }
}
