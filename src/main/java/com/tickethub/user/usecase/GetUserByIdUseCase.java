package com.tickethub.user.usecase;

import com.tickethub.user.domain.exception.UserNotFoundException;
import com.tickethub.user.domain.model.User;
import com.tickethub.user.infrastructure.controller.dto.UserResponse;
import com.tickethub.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {
    private final UserRepository userRepository;

    public UserResponse execute(Long userId){

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));

        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
