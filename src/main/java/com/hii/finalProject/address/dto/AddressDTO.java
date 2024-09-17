package com.hii.finalProject.address.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AddressDTO {
    private String recipientName;
    private String phoneNumber;
    private String addressLine;
    private Long cityId;
    private String postalCode;
    private Float lat;
    private Float lon;
}
