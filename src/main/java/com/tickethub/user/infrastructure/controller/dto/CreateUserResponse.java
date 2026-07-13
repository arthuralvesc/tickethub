package com.tickethub.user.infrastructure.controller.dto;

public record CreateUserResponse(
        Long id,
        String name,
        String email
){}
