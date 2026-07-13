package com.tickethub.user.usecase;

import com.tickethub.shared.security.JwtService;
import com.tickethub.user.domain.exception.InvalidCredentialsException;
import com.tickethub.user.domain.exception.UserNotFoundException;
import com.tickethub.user.domain.model.User;
import com.tickethub.user.infrastructure.controller.dto.LoginRequest;
import com.tickethub.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String execute(LoginRequest request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User with the specified email not found."));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid password.");
        }

        return jwtService.generateJwt(user);
    }
}
