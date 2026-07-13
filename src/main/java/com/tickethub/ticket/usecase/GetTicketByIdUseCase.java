package com.tickethub.ticket.usecase;

import com.tickethub.ticket.domain.exception.TicketNotFoundException;
import com.tickethub.ticket.infrastructure.controller.dto.TicketResponse;
import com.tickethub.ticket.infrastructure.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTicketByIdUseCase {
    private final TicketRepository ticketRepository;

    public TicketResponse execute(Long id) {
        return ticketRepository.findById(id)
                .map(TicketResponse::new)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }
}
