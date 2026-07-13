package com.tickethub.ticket.usecase;

import com.tickethub.ticket.domain.model.Booking;
import com.tickethub.ticket.infrastructure.controller.dto.BookingResponse;
import com.tickethub.ticket.infrastructure.controller.dto.TicketResponse;
import com.tickethub.ticket.infrastructure.repository.BookingRepository;
import com.tickethub.ticket.infrastructure.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllBookingsByUserIdUseCase {
    private final BookingRepository bookingRepository;

    public List<BookingResponse> execute(Long userId) {

        return bookingRepository.findByBuyerId(userId).stream()
            .map(booking -> new BookingResponse(booking.getId(),
                    booking.getBuyerId(),
                    booking.getTicket().getConcert(),
                    booking.getStatus()))
            .collect(Collectors.toList());
    }
}
