package com.tickethub.ticket.usecase;

import com.tickethub.ticket.domain.exception.BookingNotFoundException;
import com.tickethub.ticket.domain.model.Booking;
import com.tickethub.ticket.infrastructure.repository.BookingRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BookingTransactionManager {
    private final BookingRepository bookingRepository;

    public BookingTransactionManager(BookingRepository bookingRepository){
        this.bookingRepository = bookingRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmBookingTransaction(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        booking.confirm();
    }
}
