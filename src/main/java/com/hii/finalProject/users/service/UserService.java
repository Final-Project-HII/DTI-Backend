package com.hii.finalProject.users.service;

import com.hii.finalProject.users.dto.*;
import com.hii.finalProject.users.entity.User;

public interface UserService {
    User register(UserRegisterRequestDTO user);
    User registerSocial(UserRegisterSocialRequestDTO user);
    User setPassword(ManagePasswordDTO data);
    String checkVerificationLink(CheckVerificationLinkDTO data);
    void newVerificationLink(String email);
    void newResetPasswordLink(String email);
    String sendResetPasswordLink(String email);
    Boolean checkResetPasswordLinkIsValid(CheckResetPasswordLinkDTO data);
    Long getUserIdByEmail(String email);
//    void logout(LogoutRequestDTO logoutRequest);
}