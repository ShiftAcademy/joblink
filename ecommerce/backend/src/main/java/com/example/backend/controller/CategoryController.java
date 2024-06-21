package com.example.backend.controller;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.entity.Category;
import com.example.backend.service.categoryService.CategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public HttpEntity<?> getCategories(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "") String search
    ) {
        return categoryService.getCategories(page, size, search);
    }

    @GetMapping("/pcategory")
    public HttpEntity<?> getCategoriesForPodCategory() {
        return categoryService.getCategoriesForPodCategory();
    }

    @PostMapping
    public HttpEntity<?> saveCategory(
            @Valid @RequestParam("category") String category,
            @RequestParam(value = "file", required = false) MultipartFile photo
    ) throws JsonProcessingException {
        CategoryDTO categoryDTO = objectMapper.readValue(category, CategoryDTO.class);
        return categoryService.saveCategory(categoryDTO, photo);
    }

    @PutMapping("{id}")
    public HttpEntity<?> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestParam("category") String category,
            @RequestParam(value = "file", required = false) MultipartFile photo,
            @RequestParam(value = "attachmentId", required = false) UUID attachmentId
    ) throws JsonProcessingException {
        CategoryDTO categoryDTO = objectMapper.readValue(category, CategoryDTO.class);
        return categoryService.updateCategory(id, categoryDTO, photo, attachmentId);
    }

    @PutMapping("/active/{id}")
    public HttpEntity<?> updateActive(@PathVariable UUID id, @RequestParam Boolean active) {
        return categoryService.updateActive(id, active);
    }

//    @DeleteMapping("/{id}")
//    public HttpEntity<?> deleteCategory(@PathVariable UUID id) {
//        try {
//            categoryService.deleteCategory(id);
//            return ResponseEntity.ok().body("deleted successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("category is not deleted");
//        }
//    }

}
