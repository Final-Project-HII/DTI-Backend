package com.hii.finalProject.warehouse.dto;

import lombok.Data;

@Data
public class WarehouseDTO {
    private Long id;
    private String name;
    private String addressLine;
    private Integer cityId;
    private String postalCode;
    private Float lat;
    private Float lon;
}