package com.hii.finalProject.auth.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String accessToken;
    private String email;
    private String role;
}
