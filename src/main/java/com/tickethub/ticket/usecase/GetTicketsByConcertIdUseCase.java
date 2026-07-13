package com.tickethub.ticket.usecase;

import com.tickethub.ticket.infrastructure.controller.dto.TicketResponse;
import com.tickethub.ticket.infrastructure.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetTicketsByConcertIdUseCase {
    //TODO: add filter by type

    private final TicketRepository ticketRepository;

    public List<TicketResponse> execute(Long concertId) {

        return ticketRepository.findByConcertId(concertId).stream()
                .map(ticket -> new TicketResponse(ticket.getId(),
                        ticket.getIdentifier(),
                        ticket.getType(),
                        ticket.getConcert(),
                        ticket.getStatus()))
                .collect(Collectors.toList());
    }
}
