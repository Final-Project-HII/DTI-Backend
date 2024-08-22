package com.hii.finalProject.users.service;


import com.hii.finalProject.users.dto.CheckVerificationLinkDTO;
import com.hii.finalProject.users.dto.ManagePasswordDTO;
import com.hii.finalProject.users.dto.UserDTO;
import com.hii.finalProject.users.dto.UserRegisterRequestDTO;
import com.hii.finalProject.users.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByEmail(String email);
    UserDTO createUser(UserDTO userDTO);
    Optional<UserDTO> updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);

    User register(UserRegisterRequestDTO user);

    User confirmVerification(ManagePasswordDTO data);

    String checkVerificationLink(CheckVerificationLinkDTO data);


    boolean newVerificationLink(String email);

    void sendResetPasswordLink(String email);
}