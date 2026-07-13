package com.tickethub.concert.infrastructure.controller;

import com.tickethub.concert.infrastructure.controller.dto.ConcertRequest;
import com.tickethub.concert.infrastructure.controller.dto.ConcertResponse;
import com.tickethub.concert.usecase.CreateConcertUseCase;
import com.tickethub.concert.usecase.GetAllConcertsUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
public class ConcertController {
    private final CreateConcertUseCase createConcertUseCase;
    private final GetAllConcertsUseCase getAllConcertsUseCase;

    @PostMapping
    public ResponseEntity<ConcertResponse> createConcert(@RequestBody @Valid ConcertRequest request){
        ConcertResponse response = createConcertUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ConcertResponse>> getAllConcerts() {
        List<ConcertResponse> concerts = getAllConcertsUseCase.execute();

        return ResponseEntity.status(HttpStatus.OK).body(concerts);
    }
}
