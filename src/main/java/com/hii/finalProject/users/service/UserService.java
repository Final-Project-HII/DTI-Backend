package com.hii.finalProject.users.service;


import com.hii.finalProject.users.dto.*;
import com.hii.finalProject.users.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    Long getUserByEmail(String email);
    UserDTO createUser(UserDTO userDTO);
    Optional<UserDTO> updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);

    UserRegisterResponseDTO register(UserRegisterRequestDTO user);

    UserRegisterResponseDTO registerAdmin(AdminRegisterRequestDTO user);
    User registerSocial(UserRegisterSocialRequestDTO user);

    User setPassword(ManagePasswordDTO data);

    String checkVerificationLink(CheckVerificationLinkDTO data);


    void newVerificationLink(String email);

    void newResetPasswordLink(String email);

    String sendResetPasswordLink(String email);
    Boolean checkResetPasswordLinkIsValid(CheckResetPasswordLinkDTO data);

    ProfileResponseDTO updateProfile(String email, ProfileRequestDTO profileRequestDTO);

    ProfileResponseDTO getProfileData(String email);
}

