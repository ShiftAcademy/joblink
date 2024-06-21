package com.example.backend.service.categoryService;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.entity.Attachment;
import com.example.backend.entity.Category;
import com.example.backend.projection.CategoryProjection;
import com.example.backend.repositoy.AttachmentRepository;
import com.example.backend.repositoy.CategoryRepository;
import com.example.backend.service.fileService.AttachmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;


    public CategoryServiceImpl(CategoryRepository categoryRepository, AttachmentRepository attachmentRepository, AttachmentService attachmentService) {
        this.categoryRepository = categoryRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
    }


    @Override
    public HttpEntity<?> getCategories(Integer page, Integer size, String search) {
        Pageable pageable = size == -1 ? Pageable.unpaged() : PageRequest.of(page > 0 ? page  : 0, size);
        Page<Category> categories = categoryRepository.findAll(pageable);
        return ResponseEntity.ok(categories);
    }

    @Override
    public HttpEntity<?> getCategoriesForPodCategory() {
        List<CategoryProjection> categoriesForPodCategory = categoryRepository.getCategoriesForPodCategory();
        return ResponseEntity.ok(categoriesForPodCategory);
    }

    @Override
    public HttpEntity<?> saveCategory(CategoryDTO category, MultipartFile photo) {
        Category savedCategory = new Category();
        savedCategory.setId(UUID.randomUUID());
        savedCategory.setTitle(category.getTitle());
        savedCategory.setDescription(category.getDescription());
        savedCategory.setActive(category.isActive());
        savedCategory.setPhoto(saveCategoryImage(null, photo));
        savedCategory.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        return ResponseEntity.ok(categoryRepository.save(savedCategory));
    }

    @Override
    public HttpEntity<?> updateCategory(UUID id, CategoryDTO category, MultipartFile file, UUID attachmentId) {
        Category hasCategory = categoryRepository.findById(id).orElseThrow(() -> new NoSuchElementException("not found"));
        UUID previousAttachmentId = hasCategory.getPhoto() != null ?
                hasCategory.getPhoto().getId() : null;
        System.out.println(previousAttachmentId);
        hasCategory.setTitle(category.getTitle());
        hasCategory.setDescription(category.getDescription());
        if (file != null) {
            hasCategory.setPhoto(saveCategoryImage(attachmentId, file));
        }
        hasCategory.setActive(category.isActive());
        Category updatedSaved = categoryRepository.save(hasCategory);
        if (previousAttachmentId != null && file != null) {
            attachmentService.deleteAttachment(previousAttachmentId, "category");
        }
        return ResponseEntity.ok(updatedSaved);
    }

    @Override
    public HttpEntity<?> updateActive(UUID id, Boolean active) {
        Category category = categoryRepository.findById(id).orElseThrow(null);
        category.setActive(active);
        Category saved = categoryRepository.save(category);
        return ResponseEntity.ok(saved);
    }

//    @Override
//    public HttpEntity<?> deleteCategory(UUID id) {
//        try {
//            Category category = categoryRepository.findById(id).orElseThrow(null);
//            UUID previousAttachmentId = category.getPhoto() != null ?
//                    category.getPhoto().getId() : null;
//            if (previousAttachmentId != null) {
//                attachmentService.deleteAttachment(previousAttachmentId, "category");
//            }
//            categoryRepository.deleteById(id);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("category has not been deleted");
//        }
//        return ResponseEntity.ok().body("category has been deleted");
//    }

    public Attachment saveCategoryImage(UUID attachmentId, MultipartFile file) {
        if (attachmentId != null) {
            Optional<Attachment> attachment = attachmentRepository.findById(attachmentId);
            System.out.println(attachmentId);
            return attachment.orElse(null);
        } else if (file != null && !file.isEmpty()) {
            System.out.println("savecategoryimg file,getname()  " + file.getName());
            System.out.println("savecategoryimg file,getoriginalname()  " + file.getOriginalFilename());
            return attachmentService.saveAttachment(
                    "category",
                    file);
        }
        return null;
    }
}
