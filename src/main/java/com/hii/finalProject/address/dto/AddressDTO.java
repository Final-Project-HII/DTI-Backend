package com.hii.finalProject.address.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AddressDTO {
    private Long id;
    private Long userId;
    private String addressLine;
    private Long cityId;
    private String postalCode;
    private Float lat;
    private Float lon;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
