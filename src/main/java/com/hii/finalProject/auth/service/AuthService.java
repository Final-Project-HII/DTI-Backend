package com.hii.finalProject.auth.service;

import com.hii.finalProject.auth.dto.LoginResponseDTO;
import com.hii.finalProject.auth.dto.LoginSocialRequestDTO;
import com.hii.finalProject.auth.dto.LoginSocialResponseDTO;
import org.springframework.security.core.Authentication;

public interface AuthService {
    LoginResponseDTO generateToken(Authentication authentication);

    LoginSocialResponseDTO generateSocialToken(LoginSocialRequestDTO data);
}
