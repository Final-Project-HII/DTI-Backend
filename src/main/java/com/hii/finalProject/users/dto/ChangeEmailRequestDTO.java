package com.hii.finalProject.users.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeEmailRequestDTO {
    @NotBlank(message = "Email is required")
    @Email
    @NotNull
    private String newEmail;
}
