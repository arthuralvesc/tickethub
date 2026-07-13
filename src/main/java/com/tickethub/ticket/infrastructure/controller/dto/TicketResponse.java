package com.tickethub.ticket.infrastructure.controller.dto;

import com.tickethub.concert.domain.Concert;
import com.tickethub.ticket.domain.model.Ticket;
import com.tickethub.ticket.domain.model.TicketStatus;
import com.tickethub.ticket.domain.model.TicketType;

public record TicketResponse(Long id, String identifier, TicketType type, Concert concert, TicketStatus status) {

    public TicketResponse(Ticket ticket) {
        this(ticket.getId(),
                ticket.getIdentifier(),
                ticket.getType(),
                ticket.getConcert(),
                ticket.getStatus());
    }
}
