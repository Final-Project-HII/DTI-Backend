package com.hii.finalProject.users.dto;

import com.hii.finalProject.users.entity.Role;
import com.hii.finalProject.users.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class UserRegisterSocialRequestDTO {
    @NotBlank(message = "Name is required")
    @NotNull
    private String name;

    @NotBlank(message = "Email is required")
    @Email
    @NotNull
    private String email;

    @NotBlank(message = "profilePicture is required")
    @NotNull
    private String profilePicture;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    public User toEntity() {
        User user = new User();
        user.setRole(role);
        user.setEmail(email);
        user.setProfilePicture(profilePicture);
        user.setName(name);
        return user;
    }
}
