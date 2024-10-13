package com.hii.finalProject.categories.service;

import com.hii.finalProject.categories.dto.CategoriesRequestDto;
import com.hii.finalProject.categories.dto.CategoriesResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoriesService {
    CategoriesResponseDto createCategory(MultipartFile image, CategoriesRequestDto categoryRequestDto);
    CategoriesResponseDto getCategoryById(Long id);
    List<CategoriesResponseDto> getAllCategories();
    CategoriesResponseDto updateCategory(Long id,MultipartFile image, CategoriesRequestDto categoryRequestDTO);
    void deleteCategory(Long id);
}
