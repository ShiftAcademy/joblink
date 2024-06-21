package com.example.backend.service.categoryService;

import com.example.backend.dto.CategoryDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface CategoryService {

    HttpEntity<?> getCategories(Integer page, Integer size, String search);

    HttpEntity<?> getCategoriesForPodCategory();

    HttpEntity<?> saveCategory(CategoryDTO category, MultipartFile photo);

    HttpEntity<?> updateCategory(UUID id, CategoryDTO category, MultipartFile file, UUID attachmentId);

    HttpEntity<?> updateActive(UUID id, Boolean active);

//    HttpEntity<?> deleteCategory(UUID id);
}
