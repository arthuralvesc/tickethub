package com.tickethub.concert.usecase;

import com.tickethub.concert.domain.Concert;
import com.tickethub.concert.infrastructure.controller.dto.ConcertRequest;
import com.tickethub.concert.infrastructure.controller.dto.ConcertResponse;
import com.tickethub.concert.infrastructure.repository.ConcertRepository;
import com.tickethub.ticket.usecase.TicketCreationBatchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateConcertUseCase {
    private final ConcertRepository concertRepository;
    private final TicketCreationBatchUseCase ticketCreationBatchUseCase;

    public ConcertResponse execute(ConcertRequest request){
        Concert concert = new Concert(request.artist(), request.description(), request.location(), request.numberOfTickets(), LocalDateTime.now());

        concertRepository.save(concert);
        ticketCreationBatchUseCase.execute(concert.getId(), request.numberOfTickets());

        return new ConcertResponse(concert.getId(), concert.getArtist(), concert.getDescription(), concert.getLocation(), concert.getNumberOfTickets());
    }
}
