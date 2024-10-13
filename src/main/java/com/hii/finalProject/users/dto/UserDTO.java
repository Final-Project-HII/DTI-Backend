package com.hii.finalProject.users.dto;

import com.hii.finalProject.users.entity.Role;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String profilePicture;
    private Boolean isVerified;
    private Role role;
    private Integer warehouseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

