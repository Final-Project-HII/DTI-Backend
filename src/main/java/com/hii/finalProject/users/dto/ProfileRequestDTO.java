package com.hii.finalProject.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ProfileRequestDTO {
    @NotBlank(message = "Display name is required")
    private String displayName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
}
