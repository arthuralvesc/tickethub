package com.tickethub.ticket.usecase;

import com.tickethub.ticket.domain.exception.BookingNotFoundException;
import com.tickethub.ticket.domain.exception.LockNotAcquiredException;
import com.tickethub.ticket.domain.exception.UnauthorizedException;
import com.tickethub.ticket.domain.model.Booking;
import com.tickethub.ticket.domain.model.Ticket;
import com.tickethub.ticket.infrastructure.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ConfirmBookingUseCase {
    private final BookingRepository bookingRepository;
    private final BookingTransactionManager bookingTransactionManager;
    private final RedissonClient redissonClient;

    public void execute(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (!booking.getBuyerId().equals(userId))
            throw new UnauthorizedException("User is not the owner of the booking");

        Ticket ticket = booking.getTicket();
        Long ticketId = ticket.getId();
        RLock lock = redissonClient.getLock("ticket_lock" + ticketId);
        boolean acquired = false;

        try {
            acquired = lock.tryLock(10, 30, TimeUnit.SECONDS);

            if (!acquired) throw new LockNotAcquiredException("Could not acquire lock for ticket " + ticketId);

            bookingTransactionManager.confirmBookingTransaction(bookingId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockNotAcquiredException("Thread was interrupted while waiting for lock");
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
