package com.example.backend.controller;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.PodCategoryDTO;
import com.example.backend.entity.Category;
import com.example.backend.entity.PodCategory;
import com.example.backend.service.categoryService.CategoryService;
import com.example.backend.service.podCategory.PodCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/podCategory")
@RequiredArgsConstructor
public class PodCategoryController {

    private final PodCategoryService podCategoryService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //    for admin
    @GetMapping("/filter")
    public HttpEntity<?> getPodCategoriesByCategoryIds(
            @RequestParam(name = "categories", required = false) List<String> categoryIds,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        List<UUID> uuidCategoryIds = categoryIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        return podCategoryService.getPodCategoriesByFilter(page, size, uuidCategoryIds);
    }

    @PutMapping("/order")
    public HttpEntity<?> getPodCategoriesByOrder(
            @RequestParam(name = "previousIndex") Integer previous,
            @RequestParam(name = "nextIndex") Integer next
    ) {
        return podCategoryService.getPodCategoriesByOrder(previous, next);
    }

    //    for client and agent
    @GetMapping("{id}")
    public HttpEntity<?> getPodCategoryByCategoryId(
            @PathVariable() String id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        System.out.println(id);
        return podCategoryService.getPodCategoriesForAgentAndClient(page, size, id);
    }


    @PostMapping
    public HttpEntity<?> savePodCategory(
          @Valid @RequestParam("podCategory") String podCategoryDTO,
            @RequestParam(value = "file", required = false) MultipartFile photo
    ) throws JsonProcessingException {
        PodCategoryDTO podCategoryDTO1 = objectMapper.readValue(podCategoryDTO, PodCategoryDTO.class);
        return podCategoryService.savePodCategory(podCategoryDTO1, photo);
    }

    @PutMapping("{id}")
    public HttpEntity<?> updatePodCategory(
            @PathVariable UUID id,
           @Valid @RequestParam("podCategory") String podCategoryDTO,
            @RequestParam(value = "file", required = false) MultipartFile photo,
            @RequestParam(value = "attachmentId", required = false) UUID attachmentId
    ) throws JsonProcessingException {
        PodCategoryDTO podCategoryDTO1 = objectMapper.readValue(podCategoryDTO, PodCategoryDTO.class);
        return podCategoryService.updatePodCategory(podCategoryDTO1, id, photo, attachmentId);
    }

    @PutMapping("/active/{id}")
    public HttpEntity<?> updateActive(@PathVariable UUID id, @RequestParam Boolean active) {
        return podCategoryService.updateActive(id, active);
    }

}
