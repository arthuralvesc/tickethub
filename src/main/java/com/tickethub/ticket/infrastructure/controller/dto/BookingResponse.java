package com.tickethub.ticket.infrastructure.controller.dto;

import com.tickethub.concert.domain.Concert;
import com.tickethub.ticket.domain.model.Booking;
import com.tickethub.ticket.domain.model.BookingStatus;

public record BookingResponse(Long id, Long buyerId, Concert concert, BookingStatus status) {

    public BookingResponse(Booking booking) {
        this(booking.getId(),
            booking.getBuyerId(),
            booking.getTicket().getConcert(),
            booking.getStatus());
    }
}
