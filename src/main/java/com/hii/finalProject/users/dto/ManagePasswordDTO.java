package com.hii.finalProject.users.dto;

import lombok.Data;

@Data
public class ManagePasswordDTO {
    private String email;
    private String password;
    private String confirmPassword;
}
