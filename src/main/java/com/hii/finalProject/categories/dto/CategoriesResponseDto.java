package com.hii.finalProject.categories.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class CategoriesResponseDto {
    private Long id;
    private String name;
    private List<Long> products;
    private String categoryImage;
    private Instant createdAt;
    private Instant updatedAt;
}
