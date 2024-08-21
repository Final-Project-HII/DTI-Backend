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
}
