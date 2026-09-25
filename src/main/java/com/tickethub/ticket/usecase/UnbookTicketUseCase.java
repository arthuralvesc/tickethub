package com.tickethub.ticket.usecase;

import com.tickethub.shared.redisson.LockService;
import com.tickethub.ticket.domain.exception.BookingNotFoundException;
import com.tickethub.ticket.domain.exception.UnauthorizedException;
import com.tickethub.ticket.domain.model.Booking;
import com.tickethub.ticket.domain.model.BookingStatus;
import com.tickethub.ticket.domain.model.Ticket;
import com.tickethub.ticket.infrastructure.persistence.TicketPersister;
import com.tickethub.ticket.infrastructure.repository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnbookTicketUseCase {
    private final LockService lockService;
    private final TicketPersister ticketPersister;
    private final BookingRepository bookingRepository;

    public void execute(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!booking.getBuyerId().equals(userId)) throw new UnauthorizedException("User is not the owner of the booking");
        if (booking.getStatus() != BookingStatus.PENDING) throw new RuntimeException("This booking cannot be unbooked");

        Ticket ticket = booking.getTicket();
        Long ticketId = ticket.getId();

        lockService.getLock(ticketId, () -> ticketPersister.unbook(booking));
    }
}
