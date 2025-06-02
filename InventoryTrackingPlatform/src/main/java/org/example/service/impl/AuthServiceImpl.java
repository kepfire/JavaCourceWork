package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.domain.LoginRequest;
import org.example.domain.RegisterRequest;
import org.example.domain.UserDTO;
import org.example.domain.enums.Role;
import org.example.entity.UserEntity;
import org.example.exception.AuthenticationException;
import org.example.exception.UserAlreadyExistsException;
import org.example.repository.UserRepository;
import org.example.security.jwt.JwtTokenProvider;
import org.example.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        UserEntity user = userRepository.save(UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.MANAGER)
                .createdAt(LocalDateTime.now())
                .build());

        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Override
    public UserDTO loginUser(LoginRequest request) {
        Optional<UserEntity> userOpt = Optional.ofNullable(userRepository.findByEmail(request.getEmail()));
        UserEntity user = userOpt.orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            System.out.println("Password does not match for email: " + request.getEmail());
            throw new AuthenticationException("Invalid email or password");
        }

        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public String generateToken(UserDTO user) {
        Role role = userRepository.findByEmail(user.getEmail()).getRole();
        return jwtTokenProvider.createToken(user.getEmail(), role);
    }
}
