package com.hii.finalProject.users.dto;


import lombok.Data;

@Data
public class CheckVerificationLinkDTO {
    private String email;
    private String token;
}
