package com.tickethub.ticket.domain.exception;

public class TicketNotAvailableException extends RuntimeException {
    public TicketNotAvailableException(Long ticketId) {
        super("Ticket with ID " + ticketId + " is not available.");
    }
}
