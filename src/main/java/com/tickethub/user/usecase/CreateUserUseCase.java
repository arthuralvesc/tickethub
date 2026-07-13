package com.tickethub.user.usecase;

import com.tickethub.user.domain.exception.EmailAlreadyExistsException;
import com.tickethub.user.domain.model.User;
import com.tickethub.user.infrastructure.repository.UserRepository;
import com.tickethub.user.infrastructure.controller.dto.CreateUserRequest;
import com.tickethub.user.infrastructure.controller.dto.CreateUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public CreateUserResponse execute(CreateUserRequest request){
        if (userRepository.existsByEmail(request.email()))
            throw new EmailAlreadyExistsException("Email already exists.");

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.name(), request.email(), hashedPassword);

        userRepository.save(user);

        return new CreateUserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
