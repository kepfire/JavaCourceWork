package org.example.service;


import org.example.domain.LoginRequest;
import org.example.domain.RegisterRequest;
import org.example.domain.UserDTO;

public interface AuthService {
    UserDTO loginUser(LoginRequest request);

    UserDTO registerUser(RegisterRequest request);
    String generateToken(UserDTO user);
}
