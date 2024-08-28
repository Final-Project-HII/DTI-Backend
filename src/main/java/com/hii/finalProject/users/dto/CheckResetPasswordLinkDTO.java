package com.hii.finalProject.users.dto;


import lombok.Data;

@Data
public class CheckResetPasswordLinkDTO {
    private String email;
    private String token;
}
