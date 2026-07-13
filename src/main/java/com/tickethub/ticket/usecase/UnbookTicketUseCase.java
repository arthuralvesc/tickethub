package com.tickethub.ticket.usecase;

import com.tickethub.ticket.domain.exception.BookingNotFoundException;
import com.tickethub.ticket.domain.exception.TicketNotBookedException;
import com.tickethub.ticket.domain.exception.TicketNotFoundException;
import com.tickethub.ticket.domain.exception.UnauthorizedException;
import com.tickethub.ticket.domain.model.Booking;
import com.tickethub.ticket.domain.model.BookingStatus;
import com.tickethub.ticket.domain.model.Ticket;
import com.tickethub.ticket.domain.model.TicketStatus;
import com.tickethub.ticket.infrastructure.repository.BookingRepository;
import com.tickethub.ticket.infrastructure.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UnbookTicketUseCase {
    private final TicketRepository ticketRepository;
    private final RedissonClient redissonClient;
    private final BookingRepository bookingRepository;

    @Transactional
    public void execute(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!booking.getBuyerId().equals(userId)) throw new UnauthorizedException("User is not the owner of the booking");
        if (booking.getStatus() != BookingStatus.PENDING) throw new RuntimeException("This booking cannot be unbooked");

        Ticket ticket = booking.getTicket();
        Long ticketId = ticket.getId();

        RLock lock = redissonClient.getLock("ticket_lock_" + ticketId);
        boolean acquired = false;

        try {
            acquired = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!acquired) throw new RuntimeException("Could not acquire lock");

            booking.cancel();

            bookingRepository.save(booking);
            ticketRepository.save(ticket);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
