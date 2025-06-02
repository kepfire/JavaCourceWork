package org.example.service;


import org.example.domain.UserDTO;
import org.example.domain.enums.Role;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDTO createUser(UserDTO userDTO, String password, Role role);
    UserDTO getUserById(UUID id) throws UserNotFoundException;
    void updateUserPassword(UUID id, String newPassword);
    UserDTO updateUser(UUID id, UserDTO userDTO) throws UserNotFoundException;
    void deleteUser(UUID id) throws UserNotFoundException;
    boolean existsByEmail(String email);
    UserDTO findByEmail(String email) throws UserNotFoundException;
    List<UserEntity> findUsersByRole(Role role);
}
