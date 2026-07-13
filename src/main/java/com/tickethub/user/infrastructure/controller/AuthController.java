package com.tickethub.user.infrastructure.controller;

import com.tickethub.user.domain.exception.InvalidCredentialsException;
import com.tickethub.user.infrastructure.controller.dto.LoginRequest;
import com.tickethub.user.usecase.LoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request){
        try {
            String token = loginUseCase.execute(request);
            return ResponseEntity.ok(token);
        } catch (InvalidCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
