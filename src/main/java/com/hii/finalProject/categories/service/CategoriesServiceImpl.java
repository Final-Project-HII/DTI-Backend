package com.hii.finalProject.categories.service;

import com.hii.finalProject.categories.dto.CategoriesRequestDto;
import com.hii.finalProject.categories.dto.CategoriesResponseDto;
import com.hii.finalProject.categories.entity.Categories;
import com.hii.finalProject.categories.repository.CategoriesRepository;
import com.hii.finalProject.cloudinary.CloudinaryService;
import com.hii.finalProject.exceptions.UniqueNameViolationException;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.channels.MulticastChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Log
@Service
public class CategoriesServiceImpl implements CategoriesService {
    public final CategoriesRepository categoriesRepository;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "svg", "webp");
    public final CloudinaryService cloudinaryService;
    public CategoriesServiceImpl(CategoriesRepository categoriesRepository, CloudinaryService cloudinaryService){
        this.categoriesRepository = categoriesRepository;
        this.cloudinaryService = cloudinaryService;
    }
    @Override
    public CategoriesResponseDto createCategory(MultipartFile image, CategoriesRequestDto categoryRequestDto) {
        // Validasi input
        if (categoryRequestDto.getName() == null || categoryRequestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be null or empty");
        }

        String categoryName = categoryRequestDto.getName().trim();

        // Cek apakah nama kategori sudah ada
        if (categoriesRepository.existsByNameIgnoreCase(categoryName)) {
            throw new UniqueNameViolationException("Category", categoryName);
        }

        Categories categories = new Categories();
        if (image != null && !image.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(image, "category_images");
            categories.setCategoryImage(imageUrl);
        }

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
    public CategoriesResponseDto updateCategory(Long id, MultipartFile image, CategoriesRequestDto categoryRequestDTO) {
        Optional<Categories> existingCategory = categoriesRepository.findById(id);
        if (existingCategory.isPresent()) {
            Categories categories = existingCategory.get();
            categories.setName(categoryRequestDTO.getName());
            if (image != null && !image.isEmpty()) {
                try {
                    // Delete the old image if it exists
                    if (categories.getCategoryImage() != null) {
                        cloudinaryService.deleteImage(categories.getCategoryImage());
                    }
                    // Upload the new image
                    String newImageUrl = cloudinaryService.uploadFile(image, "category_images");
                    categories.setCategoryImage(newImageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to update image", e);
                }
            }
            Categories updatedCategories = categoriesRepository.save(categories);
            return mapToResponseDto(updatedCategories);
        } else {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
    }
    public void deleteCategory(Long id) {
       Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category. It has associated products.");
        }
        categoriesRepository.deleteById(id);
    }

    private CategoriesResponseDto mapToResponseDto(Categories categories){
        CategoriesResponseDto responseDto = new CategoriesResponseDto();
        responseDto.setId(categories.getId());
        responseDto.setName(categories.getName());
        responseDto.setCategoryImage(categories.getCategoryImage());
        responseDto.setCreatedAt(categories.getCreatedAt());
        responseDto.setUpdatedAt(categories.getUpdatedAt());
        responseDto.setProducts(categories.getProducts().stream().map(product -> product.getId()).collect(Collectors.toList()));
        return responseDto;
    }

}
