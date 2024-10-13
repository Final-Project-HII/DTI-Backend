package com.hii.finalProject.users.service;


import com.hii.finalProject.users.dto.*;
import com.hii.finalProject.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    Optional<UserDTO> getUserById(Long id);
    Long getUserByEmail(String email);
    UserDTO createUser(UserDTO userDTO);
    Optional<UserDTO> updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserResponseDTO updateAdmin(AdminRegisterRequestDTO adminRegisterRequestDTO);
    UserResponseDTO register(UserRegisterRequestDTO user);
    UserResponseDTO registerAdmin(AdminRegisterRequestDTO user);
    User registerSocial(UserRegisterSocialRequestDTO user);
    User setPassword(ManagePasswordDTO data);
    String checkVerificationLink(CheckVerificationLinkDTO data);
    void newVerificationLink(String email);
    void newResetPasswordLink(String email);
    String sendResetPasswordLink(String email);
    void changeEmail(String email,ChangeEmailRequestDTO changeEmailRequestDTO);
    Boolean checkResetPasswordLinkIsValid(CheckResetPasswordLinkDTO data);
    ProfileResponseDTO updateProfile(String email, ProfileRequestDTO profileRequestDTO);
    ProfileResponseDTO updateAvatar(String email, MultipartFile avatar);
    ProfileResponseDTO getProfileData(String email);
    Page<UserResponseDTO> getAllUser(String email, String role, int page, int size);
    void toggleActiveUser(Long id);
}

