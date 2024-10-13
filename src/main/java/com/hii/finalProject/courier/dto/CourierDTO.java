package com.hii.finalProject.courier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierDTO {
    private Long id;
    private String name;
    private int cost;
}
