package com.example.backend.service.podCategory;

import com.example.backend.dto.PodCategoryDTO;
import com.example.backend.entity.Attachment;
import com.example.backend.entity.Category;
import com.example.backend.entity.PodCategory;
import com.example.backend.entity.Product;
import com.example.backend.projection.PodCategoryProjection;
import com.example.backend.projection.PodCategoryResponseProjection;
import com.example.backend.repositoy.AttachmentRepository;
import com.example.backend.repositoy.CategoryRepository;
import com.example.backend.repositoy.PodCategoryRepository;
import com.example.backend.service.fileService.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PodCategoryServiceImpl implements PodCategoryService {

    private final PodCategoryRepository podCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;


    public PodCategoryServiceImpl(PodCategoryRepository podCategoryRepository, CategoryRepository categoryRepository, AttachmentRepository attachmentRepository, AttachmentService attachmentService) {
        this.podCategoryRepository = podCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
    }

    //    byAdmin
    @Override
    public HttpEntity<?> getPodCategoriesByFilter(Integer page, Integer size, List<UUID> categoryIds) {
        Pageable pageable = size == -1 ? Pageable.unpaged() : PageRequest.of(page > 0 ? page : 0, size);
        Page<List<PodCategoryResponseProjection>> podCategories = podCategoryRepository.getAllPodCategoriesByCategoryIds(categoryIds, pageable);

        return ResponseEntity.ok(podCategories);
    }


    //    by agent and client
    @Override
    public HttpEntity<?> getPodCategoriesForAgentAndClient(Integer page, Integer size, String categoryId) {
        if (categoryId.equals("0")) {
            List<PodCategoryProjection> allPodCategories = podCategoryRepository.getAllPodCategories();
            return ResponseEntity.ok(allPodCategories);
        }
        List<PodCategoryProjection> podCategoriesByCategoryId = podCategoryRepository.getAllPodCategoriesByCategoryId(UUID.fromString(categoryId));
        return ResponseEntity.ok(podCategoriesByCategoryId);
    }

    @Transactional
    @Override
    public HttpEntity<?> getPodCategoriesByOrder(Integer pick, Integer put) {
        if (pick == null || put == null) {
            // Handle invalid input, e.g., throw an exception or return an error response
            return ResponseEntity.ok().body("Error because order indexes are null");
        }

        List<PodCategory> podCategories = podCategoryRepository.getAllPodCategoriesByOrder();
        for (PodCategory podCategory : podCategories) {
            System.out.println(" order qilayotganda dbdan kelgan podcategorylar");
            System.out.println(podCategory.getTitle() + " " + podCategory.getOrdered());
        }
        ordered(podCategories, pick, put);
        // Return a success response
        return ResponseEntity.ok("PodCategories reordered successfully");
    }


    @Override
    public HttpEntity<?> savePodCategory(PodCategoryDTO podCategoryDTO, MultipartFile photo) {
        Category category = categoryRepository.findById(UUID.fromString(podCategoryDTO.getCategory_id())).orElseThrow(null);
        List<PodCategory> podCategories = podCategoryRepository.forMe();
        for (PodCategory podCategory : podCategories) {
            System.out.println("podcategories forme querydan qaytgan");
            System.out.println(podCategory.getTitle() + " " + podCategory.getOrdered());
            System.out.println("podcategories forme querydan qaytgan");
        }
        PodCategory podCategory = new PodCategory();
        if (podCategories.size() != 0) {
            PodCategory lastPodCategory = podCategories.get(0);
            System.out.println("------podcategory savedagi if----------");
            System.out.println(lastPodCategory.getOrdered());
            System.out.println("------podcategory savedagi if----------");
            podCategory.setOrdered(lastPodCategory.getOrdered() + 1);


        } else {
            podCategory.setOrdered(0);
            System.out.println("---------podcategory savedagi else---------");
            System.out.println(podCategory.getOrdered());
            System.out.println("---------podcategory savedagi else---------");
        }
        podCategory.setId(UUID.randomUUID());
        podCategory.setTitle(podCategoryDTO.getTitle());
        podCategory.setDescription(podCategoryDTO.getDescription());
        podCategory.setActive(podCategoryDTO.isActive());
        podCategory.setCategory(category);
        podCategory.setPhoto(savePodCategoryImage(null, photo));
        podCategory.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        PodCategory save = podCategoryRepository.save(podCategory);
        return ResponseEntity.ok(save);
    }

    @Override
    public HttpEntity<?> updatePodCategory(PodCategoryDTO podCategory, UUID id, MultipartFile photo, UUID attachmentId) {
        Category category = categoryRepository.findById(UUID.fromString(podCategory.getCategory_id())).orElseThrow(null);
        PodCategory findedPodCategory = podCategoryRepository.findById(id).orElseThrow(null);
        UUID previousAttachmentId = findedPodCategory.getPhoto() != null ?
                findedPodCategory.getPhoto().getId() : null;
        findedPodCategory.setTitle(podCategory.getTitle());
        findedPodCategory.setDescription(podCategory.getDescription());
        findedPodCategory.setActive(podCategory.isActive());
        if (photo != null) {
            findedPodCategory.setPhoto(savePodCategoryImage(attachmentId, photo));
        }
        findedPodCategory.setCategory(category);
        if (previousAttachmentId != null && photo != null) {
            attachmentService.deleteAttachment(previousAttachmentId, "podCategory");
        }
        return ResponseEntity.ok(podCategoryRepository.save(findedPodCategory));
    }

    @Override
    public HttpEntity<?> updateActive(UUID id, Boolean active) {
        PodCategory podCategory = podCategoryRepository.findById(id).orElseThrow(null);
        podCategory.setActive(active);
        return ResponseEntity.ok(podCategoryRepository.save(podCategory));
    }

    public Attachment savePodCategoryImage(UUID attachmentId, MultipartFile file) {
        if (attachmentId != null) {
            Optional<Attachment> attachment = attachmentRepository.findById(attachmentId);
            return attachment.orElse(null);
        } else if (file != null && !file.isEmpty()) {
            return attachmentService.saveAttachment(
                    "podCategory",
                    file);
        }
        return null;
    }

    public void ordered(List<PodCategory> podCategories, Integer pick, Integer put) {
        PodCategory pickPodCategory = null;
        PodCategory putPodCategory = null;

        for (PodCategory podCategory : podCategories) {
            if (podCategory.getOrdered() == pick) {
                pickPodCategory = podCategory;
            } else if (podCategory.getOrdered() == put) {
                putPodCategory = podCategory;
            }
        }

        if (pickPodCategory != null && putPodCategory != null) {
            if (pick > put) {
                pickPodCategory.setOrdered(putPodCategory.getOrdered());
                podCategoryRepository.save(pickPodCategory);
                while (pick > put) {
                    PodCategory podCategory = podCategories.get(put);
                    podCategory.setOrdered(put + 1);
                    podCategoryRepository.save(podCategory);
                    put++;
                }
            } else if (put > pick) {
                pickPodCategory.setOrdered(putPodCategory.getOrdered());
                podCategoryRepository.save(pickPodCategory);
                while (pick < put) {
                    PodCategory podCategory = podCategories.get(put);
                    podCategory.setOrdered(put - 1);
                    podCategoryRepository.save(podCategory);
                    put--;
                }
            }
        }
    }

}
