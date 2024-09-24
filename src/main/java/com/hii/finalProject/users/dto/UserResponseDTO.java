package com.hii.finalProject.users.dto;

import com.hii.finalProject.users.entity.Role;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private Long warehouseId;
    private Boolean isVerified;
    private String email;
    private Role role;
    private Boolean isActive;
}
