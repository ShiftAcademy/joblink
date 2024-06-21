package com.example.backend.service.productService;

import com.example.backend.dto.ProductDTO;
import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {


    HttpEntity<?> getProductsByFilter(List<UUID> podCategoryIds, Integer page, Integer size);

    HttpEntity<?> getProductsByPodCategoryId(UUID id);

    HttpEntity<?> productReorder(UUID podCategoryId, Integer previous, Integer next);

    HttpEntity<?> saveProduct(ProductDTO productDTO, MultipartFile photo);

    HttpEntity<?> updateProduct(ProductDTO product, UUID id, MultipartFile photo, UUID attachmentId);

    HttpEntity<?> updateActive(UUID id,Boolean active);

    HttpEntity<?> deleteProduct(UUID id);

}
