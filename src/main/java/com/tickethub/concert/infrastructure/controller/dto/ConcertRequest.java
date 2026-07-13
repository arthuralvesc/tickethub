package com.tickethub.concert.infrastructure.controller.dto;

import lombok.NonNull;

public record ConcertRequest (
        @NonNull String artist,
        @NonNull String description,
        @NonNull String location,
        @NonNull Integer numberOfTickets){
}
