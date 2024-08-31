package com.hii.finalProject.categories.service;

import com.hii.finalProject.categories.dto.CategoriesRequestDto;
import com.hii.finalProject.categories.dto.CategoriesResponseDto;

import java.util.List;

public interface CategoriesService {
    CategoriesResponseDto createCategory(CategoriesRequestDto categoryRequestDto);
    CategoriesResponseDto getCategoryById(Long id);
    List<CategoriesResponseDto> getAllCategories();
    CategoriesResponseDto updateCategory(Long id, CategoriesRequestDto categoryRequestDTO);
    void deleteCategory(Long id);
}
