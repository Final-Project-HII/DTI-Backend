package com.hii.finalProject.auth.dto;


import lombok.Data;

@Data
public class LoginSocialResponseDTO {
    private String email;
    private String role;
    private String accessToken;
}
