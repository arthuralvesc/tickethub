package com.tickethub.user.infrastructure.controller;

import com.tickethub.user.infrastructure.controller.dto.CreateUserRequest;
import com.tickethub.user.infrastructure.controller.dto.CreateUserResponse;
import com.tickethub.user.infrastructure.controller.dto.UserResponse;
import com.tickethub.user.usecase.CreateUserUseCase;
import com.tickethub.user.usecase.GetUserByIdUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request){
        CreateUserResponse user = createUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@RequestParam Long userId){
        UserResponse user = getUserByIdUseCase.execute(userId);
        return ResponseEntity.ok(user);
    }
}
