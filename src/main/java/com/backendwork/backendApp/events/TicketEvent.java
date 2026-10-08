package com.backendwork.backendApp.events;

public class TicketEvent {

    private String ticketId;
    private String customerId;
    private String customerEmail;
    private String eventType;
    private String message;

    public TicketEvent() {
    }

    public TicketEvent(
            String ticketId,
            String customerId,
            String customerEmail,
            String eventType,
            String message
    ) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.eventType = eventType;
        this.message = message;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}