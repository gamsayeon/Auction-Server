package com.example.auction_server.service;

import com.example.auction_server.dto.UserDTO;
import com.example.auction_server.enums.UserType;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO) throws AccessDeniedException;

    UserDTO registerAdminUser(UserDTO userDTO);

    boolean checkDuplicationUserId(String userId);

    boolean checkDuplicationEmail(String email);

    boolean emailAndIdMatched(Long id, String email);

    UserDTO loginUser(UserDTO userDTO, HttpSession session);

    void insertSession(HttpSession session, Long id, UserType userType);

    UserDTO selectUser(Long id);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void withDrawUser(Long id);

    void logoutUser(HttpSession session);
}