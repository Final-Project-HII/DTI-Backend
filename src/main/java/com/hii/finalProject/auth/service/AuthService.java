package com.hii.finalProject.auth.service;

import com.hii.finalProject.auth.dto.LoginResponseDTO;
import org.springframework.security.core.Authentication;

public interface AuthService {
    LoginResponseDTO generateToken(Authentication authentication);
}
