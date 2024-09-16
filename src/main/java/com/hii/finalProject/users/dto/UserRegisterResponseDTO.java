package com.hii.finalProject.users.dto;

import com.hii.finalProject.users.entity.Role;
import lombok.Data;

@Data
public class UserRegisterResponseDTO {
    private String name;
    private String imageUrl;
    private Boolean isVerified;
    private String email;
    private Role role;
}
