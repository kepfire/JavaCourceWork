package org.example.service;

import org.example.domain.UserDTO;
import org.example.domain.enums.Role;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.repository.UserRepository;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    private UUID userId;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder);

        userId = UUID.randomUUID();
        user = new UserEntity();
        user.setUserId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
    }

    @Test
    void testCreateUser_Success() {
        UserDTO userDTO = UserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .build();

        when(passwordEncoder.encode("password")).thenReturn("hashedpassword");
        when(userRepository.save(any())).thenReturn(user);

        UserDTO result = userService.createUser(userDTO, "password", Role.ADMIN);

        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserById(userId);

        assertEquals(userId, result.getUserId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testUpdateUserPassword_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("newhashedpassword");

        userService.updateUserPassword(userId, "newpassword");

        assertEquals("newhashedpassword", user.getPasswordHash());
    }
}
