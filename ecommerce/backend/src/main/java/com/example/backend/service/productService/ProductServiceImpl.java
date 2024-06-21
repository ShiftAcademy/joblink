package com.example.backend.service.productService;

import com.example.backend.dto.ProductDTO;
import com.example.backend.entity.Attachment;
import com.example.backend.entity.PodCategory;
import com.example.backend.entity.Product;
import com.example.backend.projection.ProductProjection;
import com.example.backend.projection.ProductProjection2;
import com.example.backend.repositoy.AttachmentRepository;
import com.example.backend.repositoy.PodCategoryRepository;
import com.example.backend.repositoy.ProductRepository;
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
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PodCategoryRepository podCategoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;

    public ProductServiceImpl(ProductRepository productRepository, PodCategoryRepository podCategoryRepository, AttachmentRepository attachmentRepository, AttachmentService attachmentService) {
        this.productRepository = productRepository;
        this.podCategoryRepository = podCategoryRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
    }


    @Override
    public HttpEntity<?> getProductsByFilter(List<UUID> podCategoryIds, Integer page, Integer size) {
        Pageable pageable = size == -1 ? Pageable.unpaged() : PageRequest.of(page > 0 ? page  : 0, size);

        if (podCategoryIds.isEmpty()) {
            Page<ProductProjection> products = productRepository.getAllProducts(pageable);
            return ResponseEntity.ok(products);
        }
        Page<ProductProjection> filteredProducts = productRepository.findAllByTitleAndDescriptionAndPodCategoryIds(podCategoryIds, pageable);
        System.out.println(filteredProducts);
        return ResponseEntity.ok(filteredProducts);
    }

    @Override
    public HttpEntity<?> getProductsByPodCategoryId(UUID id) {
        List<ProductProjection2> products = productRepository.getProductsForFrontSelect(id);
        return ResponseEntity.ok(products);
    }

    @Override
    public HttpEntity<?> productReorder(UUID podCategoryId, Integer pick, Integer put) {
        if (pick == null || put == null) {
            // Handle invalid input, e.g., throw an exception or return an error response
            return ResponseEntity.ok().body("Error because order is null");
        }
        List<Product> products = productRepository.getAllProductsByPodCategoryId(podCategoryId);
        for (Product product : products) {
            System.out.println("dbdagi 1ta pocategoryga tegishli productla");
            System.out.println(product.getTitle() + " " + product.getOrdered());
            System.out.println("dbdagi 1ta pocategoryga tegishli productla");
        }
        productReordered(products, pick, put);
//        return ResponseEntity.ok("products reordered successfully");
        return ResponseEntity.ok("Products reordered successfully");
    }

    @Override
    public HttpEntity<?> saveProduct(ProductDTO productDTO, MultipartFile photo) {
        PodCategory podCategory = podCategoryRepository.findById(UUID.fromString(productDTO.getPodCategory_id())).orElseThrow(null);
        Product product = new Product();
        List<Product> products = productRepository.forMe(podCategory.getId());
        if (products.size() != 0) {
            Product lastProduct = products.get(0);
            product.setOrdered(lastProduct.getOrdered() + 1);
        } else {
            product.setOrdered(0);
        }
        product.setId(UUID.randomUUID());
        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setPodCategory(podCategory);
        product.setActive(productDTO.isActive());
        product.setPhoto(saveProductImage(null, photo));
        product.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        Product save = productRepository.save(product);
        return ResponseEntity.ok(save);
    }

    @Override
    public HttpEntity<?> updateProduct(ProductDTO product, UUID id, MultipartFile photo, UUID attachmentId) {
        PodCategory podCategory = podCategoryRepository.findById(UUID.fromString(product.getPodCategory_id())).orElseThrow(null);
        Product hasProduct = productRepository.findById(id).orElseThrow(null);
        UUID previousAttachmentId = hasProduct.getPhoto() != null ?
                hasProduct.getPhoto().getId() : null;
        hasProduct.setTitle(product.getTitle());
        hasProduct.setDescription(product.getDescription());
        hasProduct.setPodCategory(podCategory);
        if (photo != null) {
            hasProduct.setPhoto(saveProductImage(attachmentId, photo));
        }
        hasProduct.setPrice(product.getPrice());
        hasProduct.setActive(product.isActive());
        Product saved = productRepository.save(hasProduct);
        if (previousAttachmentId != null && photo != null) {
            attachmentService.deleteAttachment(previousAttachmentId, "product");
        }
        return ResponseEntity.ok(saved);
    }

    @Override
    public HttpEntity<?> updateActive(UUID id, Boolean active) {
        Product product = productRepository.findById(id).orElseThrow(null);
        product.setActive(active);
        return ResponseEntity.ok(productRepository.save(product));
    }

    @Override
    public HttpEntity<?> deleteProduct(UUID id) {
        try {
            Product product = productRepository.findById(id).orElseThrow(null);
            UUID previousAttachmentId = product.getPhoto() != null ?
                    product.getPhoto().getId() : null;
            System.out.println(previousAttachmentId + "-product's deleted attachment id");
            reorderAfterDeleteProduct(product);
            productRepository.deleteById(id);
            if (previousAttachmentId != null) {
                attachmentService.deleteAttachment(previousAttachmentId, "product");
            }
            return ResponseEntity.ok().body("");
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok().body("deleted error!");
        }
    }

    public Attachment saveProductImage(UUID attachmentId, MultipartFile file) {
        if (attachmentId != null) {
            Optional<Attachment> attachment = attachmentRepository.findById(attachmentId);
            return attachment.orElse(null);
        } else if (file != null && !file.isEmpty()) {
            return attachmentService.saveAttachment(
                    "product",
                    file);
        }
        return null;
    }

    public void productReordered(List<Product> products, Integer pick, Integer put) {
        Product pickProduct = null;
        Product putProduct = null;

        for (Product product : products) {
            if (product.getOrdered() == pick) {
                pickProduct = product;
            } else if (product.getOrdered() == put) {
                putProduct = product;
            }
        }

        if (pickProduct != null && putProduct != null) {
            if (pick > put) {
                pickProduct.setOrdered(putProduct.getOrdered());
                productRepository.save(pickProduct);
                while (pick > put) {
                    Product product = products.get(put);
                    product.setOrdered(put + 1);
                    productRepository.save(product);
                    put++;
                }
            } else if (put > pick) {
                pickProduct.setOrdered(putProduct.getOrdered());
                productRepository.save(pickProduct);
                while (pick < put) {
                    Product product = products.get(put);
                    product.setOrdered(put - 1);
                    productRepository.save(product);
                    put--;
                }
            }
        }

    }

    public void reorderAfterDeleteProduct(Product product) {
        UUID podCategoryId = product.getPodCategory() != null ? product.getPodCategory().getId() : null;
        System.out.println(podCategoryId + "-podcategoryid reorderafterdeleteproduct");
        Integer removedOrder = product.getOrdered();
        System.out.println(removedOrder + "-removeorder");
        if (podCategoryId != null) {
            List<Product> products = productRepository.getAllProductsByPodCategoryId(podCategoryId);
            while (products.size() - 1 > removedOrder) {
                products.get(removedOrder + 1).setOrdered(removedOrder);
                removedOrder++;
            }
        }

    }
}
