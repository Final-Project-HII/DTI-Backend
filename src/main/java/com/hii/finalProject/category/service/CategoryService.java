package com.hii.finalProject.category.service;

import com.hii.finalProject.category.dto.CategoryDTO;
import com.hii.finalProject.category.entity.Category;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(Long id);
}
