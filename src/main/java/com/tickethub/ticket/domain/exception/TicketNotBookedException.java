package com.tickethub.ticket.domain.exception;

public class TicketNotBookedException extends RuntimeException {
    public TicketNotBookedException(Long ticketId) {
        super("Ticket with ID " + ticketId + " is not booked.");
    }
}