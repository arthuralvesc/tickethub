package com.tickethub.ticket.infrastructure.controller;

import com.sun.security.auth.UserPrincipal;
import com.tickethub.ticket.domain.model.Ticket;
import com.tickethub.ticket.infrastructure.controller.dto.BookingResponse;
import com.tickethub.ticket.infrastructure.controller.dto.TicketResponse;
import com.tickethub.ticket.infrastructure.repository.TicketRepository;
import com.tickethub.ticket.usecase.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final BookTicketUseCase bookTicketUseCase;
    private final UnbookTicketUseCase unbookTicketUseCase;
    private final ConfirmBookingUseCase confirmBookingUseCase;
    private final GetTicketByIdUseCase getTicketByIdUseCase;
    private final GetTicketsByConcertIdUseCase getTicketsByConcertIdUseCase;

    @PostMapping("/{ticketId}/book")
    public ResponseEntity<Void> bookTicket(@PathVariable Long ticketId, @RequestParam Long userId) {
        bookTicketUseCase.execute(ticketId, userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ticketId}/unbook")
    public ResponseEntity<Void> unbookTicket(@PathVariable Long bookingId, @RequestParam Long userId) {
        unbookTicketUseCase.execute(bookingId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ticketId}/confirm")
    public ResponseEntity<Void> confirmBooking(@PathVariable Long bookingId, @RequestParam Long userId) {
        confirmBookingUseCase.execute(bookingId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long ticketId, @RequestParam Long userId){
         TicketResponse ticket = getTicketByIdUseCase.execute(ticketId);
         return ResponseEntity.ok(ticket);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getTicketsByConcertId(@RequestParam Long concertId) {
        List<TicketResponse> tickets = getTicketsByConcertIdUseCase.execute(concertId);
        return ResponseEntity.ok(tickets);
    }
}
