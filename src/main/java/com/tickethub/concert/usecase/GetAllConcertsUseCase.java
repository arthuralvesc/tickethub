package com.tickethub.concert.usecase;

import com.tickethub.concert.infrastructure.controller.ConcertController;
import com.tickethub.concert.infrastructure.controller.dto.ConcertResponse;
import com.tickethub.concert.infrastructure.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetAllConcertsUseCase {
    private final ConcertRepository concertRepository;

    public List<ConcertResponse> execute(){
        return concertRepository.findAll().stream()
                .map(concert -> new ConcertResponse(
                        concert.getId(),
                        concert.getDescription(),
                        concert.getArtist(),
                        concert.getLocation()
                )).collect(Collectors.toList());
    }
}
