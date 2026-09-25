package com.tickethub.ticket.infrastructure.persistence;

import com.tickethub.ticket.domain.exception.TicketNotAvailableException;
import com.tickethub.ticket.domain.exception.TicketNotFoundException;
import com.tickethub.ticket.domain.model.Booking;
import com.tickethub.ticket.domain.model.Ticket;
import com.tickethub.ticket.domain.model.TicketStatus;
import com.tickethub.ticket.infrastructure.repository.BookingRepository;
import com.tickethub.ticket.infrastructure.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketPersister {
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public void book(Long ticketId, Long userId){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getStatus() != TicketStatus.AVAILABLE) throw new TicketNotAvailableException(ticketId);

        Booking booking = new Booking(userId, ticket);
        ticket.setStatus(TicketStatus.BOOKED);

        ticketRepository.save(ticket);
        bookingRepository.save(booking);
    }

    @Transactional
    public void unbook(Booking booking){
        Ticket ticket = booking.getTicket();
        booking.cancel();

        bookingRepository.save(booking);
        ticketRepository.save(ticket);
    }
}
