package com.tickethub.ticket.domain.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long BookingId) {
        super("Booking with ID " + BookingId + " not found.");
    }
}
