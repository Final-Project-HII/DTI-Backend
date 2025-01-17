package com.hii.finalProject.categories.controller;

import com.hii.finalProject.categories.dto.CategoriesRequestDto;
import com.hii.finalProject.categories.dto.CategoriesResponseDto;
import com.hii.finalProject.categories.service.CategoriesService;
import com.hii.finalProject.products.dto.ProductListDtoResponse;
import com.hii.finalProject.response.Response;
import com.hii.finalProject.validation.ValidImage;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoriesController {
    private final CategoriesService categoriesService;
    public CategoriesController(CategoriesService categoriesService){
        this.categoriesService = categoriesService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<Response<CategoriesResponseDto>> createCategory(@RequestParam("categoriesData") String categoriesData,
                                                                         @RequestParam("image") @ValidImage(maxSizeInMB = 2) MultipartFile image) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        CategoriesRequestDto categoriesRequestDto = gson.fromJson(categoriesData, CategoriesRequestDto.class);
        CategoriesResponseDto createCategory = categoriesService.createCategory(image, categoriesRequestDto);
        return Response.successfulResponse(HttpStatus.CREATED.value(), "Category created successfully", createCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoriesResponseDto>> getAllCategories(){
        return ResponseEntity.ok(categoriesService.getAllCategories());
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<CategoriesResponseDto> updateCategory(
            @PathVariable Long id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("categoriesData") String categoryData) {

        Gson gson = new GsonBuilder().create();
        CategoriesRequestDto categoriesRequestDto = gson.fromJson(categoryData, CategoriesRequestDto.class);

        try {
            CategoriesResponseDto updatedCategory = categoriesService.updateCategory(id, image, categoriesRequestDto);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('SCOPE_SUPER')")
    public ResponseEntity<Response<Void>> deleteCategory(@PathVariable Long id) {
    try {
        categoriesService.deleteCategory(id);
        return Response.successfulResponse("Category deleted successfully",null);
    } catch (IllegalArgumentException e) {
        return Response.failedResponse(HttpStatus.NOT_FOUND.value(), "Category not found", null);
    } catch (IllegalStateException e) {
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    } catch (Exception e) {
        return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred", null);
    }
}
}
