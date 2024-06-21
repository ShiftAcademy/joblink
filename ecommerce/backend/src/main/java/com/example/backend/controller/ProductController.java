package com.example.backend.controller;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.ProductDTO;
import com.example.backend.entity.Product;
import com.example.backend.service.productService.ProductService;
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
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/filter")
    public HttpEntity<?> getProductsByFilter(
            @RequestParam(name = "podCategories", required = false) List<String> podCategoryIds,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        System.out.println(podCategoryIds);
        List<UUID> uuidPodCategoryIds = podCategoryIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        return productService.getProductsByFilter(uuidPodCategoryIds, page, size);
    }

    @GetMapping("{id}")
    public HttpEntity<?> getProductsByPodCategoryId(@PathVariable UUID id) {
        return productService.getProductsByPodCategoryId(id);
    }

    @PostMapping
    public HttpEntity<?> saveProduct(
            @Valid @RequestParam(name = "product") String product,
            @RequestParam(value = "file", required = false) MultipartFile photo
    ) throws JsonProcessingException {
        ProductDTO productDTO = objectMapper.readValue(product, ProductDTO.class);
        return productService.saveProduct(productDTO, photo);
    }

    @PutMapping("/order")
    public HttpEntity<?> productReorder(
            @RequestParam(name = "podCategoryId") UUID podCategoryId,
            @RequestParam(name = "previousIndex") Integer previous,
            @RequestParam(name = "nextIndex") Integer next
    ) {
        return productService.productReorder(podCategoryId, previous, next);
    }


    @PutMapping("{id}")
    public HttpEntity<?> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestParam("product") String product,
            @RequestParam(value = "file", required = false) MultipartFile photo,
            @RequestParam(value = "attachmentId", required = false) UUID attachmentId
    ) throws JsonProcessingException {
        ProductDTO productDTO = objectMapper.readValue(product, ProductDTO.class);
        return productService.updateProduct(productDTO, id, photo, attachmentId);
    }

    @PutMapping("/active/{id}")
    public HttpEntity<?> updateActive(@PathVariable UUID id, @RequestParam Boolean active) {
        return productService.updateActive(id, active);
    }

    @DeleteMapping("{id}")
    public HttpEntity<?> deleteProduct(@PathVariable UUID id) {
        return productService.deleteProduct(id);
    }

}
