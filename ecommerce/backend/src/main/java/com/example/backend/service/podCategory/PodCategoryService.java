package com.example.backend.service.podCategory;

import com.example.backend.dto.PodCategoryDTO;
import com.example.backend.entity.PodCategory;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PodCategoryService {

    HttpEntity<?> getPodCategoriesByFilter(Integer page, Integer size, List<UUID> categoryIds);

    HttpEntity<?> getPodCategoriesForAgentAndClient(Integer page, Integer size, String categoryId);

    HttpEntity<?> getPodCategoriesByOrder(Integer previous, Integer next);

    HttpEntity<?> savePodCategory(PodCategoryDTO podCategory, MultipartFile photo);

    HttpEntity<?> updatePodCategory(PodCategoryDTO podCategory, UUID id, MultipartFile photo, UUID attachmentId);

    HttpEntity<?> updateActive(UUID id, Boolean active);

}
