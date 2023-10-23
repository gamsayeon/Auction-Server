package com.ccommit.auction_server.service;

import com.ccommit.auction_server.dto.UserDTO;
import com.ccommit.auction_server.enums.UserType;
import jakarta.servlet.http.HttpSession;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);

    UserDTO updateUserType(String userId);

    UserDTO loginUser(UserDTO userDTO, HttpSession session);

    void insertSession(HttpSession session, Long id, UserType userType);

    UserDTO selectUser(Long id);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void withDrawUser(Long id);

    void logoutUser(HttpSession session);
}
