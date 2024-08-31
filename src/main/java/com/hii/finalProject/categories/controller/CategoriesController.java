package com.hii.finalProject.categories.controller;

import com.hii.finalProject.categories.dto.CategoriesRequestDto;
import com.hii.finalProject.categories.dto.CategoriesResponseDto;
import com.hii.finalProject.categories.service.CategoriesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoriesController {
    private final CategoriesService categoriesService;
    public CategoriesController(CategoriesService categoriesService){
        this.categoriesService = categoriesService;
    }

    @PostMapping("/create")
    public ResponseEntity<CategoriesResponseDto> createCategory(@RequestBody CategoriesRequestDto categoriesRequestDto){
        return new ResponseEntity<>(categoriesService.createCategory(categoriesRequestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoriesResponseDto>> getAllCategories(){
        return ResponseEntity.ok(categoriesService.getAllCategories());
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<CategoriesResponseDto> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoriesRequestDto categoriesRequestDto) {
        try {
            CategoriesResponseDto updatedCategory = categoriesService.updateCategory(id, categoriesRequestDto);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoriesService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
