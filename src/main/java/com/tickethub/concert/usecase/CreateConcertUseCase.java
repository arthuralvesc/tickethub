package com.tickethub.concert.usecase;

import com.tickethub.concert.domain.Concert;
import com.tickethub.concert.infrastructure.controller.dto.ConcertRequest;
import com.tickethub.concert.infrastructure.controller.dto.ConcertResponse;
import com.tickethub.concert.infrastructure.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateConcertUseCase {
    private final ConcertRepository concertRepository;

    public ConcertResponse execute(ConcertRequest request){
        Concert concert = new Concert(request.artist(), request.description(), request.location(), LocalDateTime.now());

        concertRepository.save(concert);

        return new ConcertResponse(concert.getId(), concert.getArtist(), concert.getDescription(), concert.getLocation());
    }
}
