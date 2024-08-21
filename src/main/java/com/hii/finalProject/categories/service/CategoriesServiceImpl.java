package com.hii.finalProject.categories.service;

import com.hii.finalProject.categories.dto.CategoriesRequestDto;
import com.hii.finalProject.categories.dto.CategoriesResponseDto;
import com.hii.finalProject.categories.entity.Categories;
import com.hii.finalProject.categories.repository.CategoriesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriesServiceImpl implements CategoriesService {
    public final CategoriesRepository categoriesRepository;
    public CategoriesServiceImpl(CategoriesRepository categoriesRepository){
        this.categoriesRepository = categoriesRepository;
    }
    @Override
    public CategoriesResponseDto createCategory(CategoriesRequestDto categoryRequestDto) {
        Categories categories = new Categories();
        categories.setName(categoryRequestDto.getName());
        Categories savedCategories = categoriesRepository.save(categories);
        return mapToResponseDto(savedCategories);
    }

    @Override
    public CategoriesResponseDto getCategoryById(Long id) {
        return null;
    }

    @Override
    public List<CategoriesResponseDto> getAllCategories() {
        List<Categories> categories = categoriesRepository.findAll();
        return categories.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    @Override
    public CategoriesResponseDto updateCategory(Long id, CategoriesRequestDto categoryRequestDTO) {
        return null;
    }

    @Override
    public void deleteCategory(Long id) {

    }

    private CategoriesResponseDto mapToResponseDto(Categories categories){
        CategoriesResponseDto responseDto = new CategoriesResponseDto();
        responseDto.setId(categories.getId());
        responseDto.setName(categories.getName());
        responseDto.setCreatedAt(categories.getCreatedAt());
        responseDto.setUpdatedAt(categories.getUpdatedAt());
        responseDto.setProducts(categories.getProducts().stream().map(product -> product.getId()).collect(Collectors.toList()));
        return responseDto;
    }
}
