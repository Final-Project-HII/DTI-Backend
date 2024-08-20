package com.hii.finalProject.courier.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CourierDTO {

    private Long id;
    private Integer originCityId;
    private Integer destinationCityId;
    private String courier;
    private Integer weight;
    private Integer price;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
