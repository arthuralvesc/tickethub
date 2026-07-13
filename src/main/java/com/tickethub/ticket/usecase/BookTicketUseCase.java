package com.tickethub.ticket.usecase;

import com.tickethub.ticket.domain.exception.TicketNotAvailableException;
import com.tickethub.ticket.domain.exception.TicketNotFoundException;
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
public class BookTicketUseCase {
    private final RedissonClient redissonClient;
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public void execute(Long ticketId, Long userId) {
        RLock lock = redissonClient.getLock("ticket_lock_" + ticketId);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if (!acquired) throw new RuntimeException("Could not acquire lock");

            Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

            if (ticket.getStatus() != TicketStatus.AVAILABLE) throw new TicketNotAvailableException(ticketId);

            Booking booking = new Booking(userId, ticket);
            ticket.setStatus(TicketStatus.BOOKED);

            ticketRepository.save(ticket);
            bookingRepository.save(booking);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);
        }
         finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
