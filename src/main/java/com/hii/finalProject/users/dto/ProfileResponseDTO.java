package com.hii.finalProject.users.dto;


import lombok.Data;

@Data
public class ProfileResponseDTO {
    private String email;
    private Long warehouseId;
    private String displayName;
    private String phoneNumber;
    private String avatar;
}
