package com.example.backend.repositoy;

import com.example.backend.entity.PodCategory;
import com.example.backend.projection.PodCategoryProjection;
import com.example.backend.projection.PodCategoryResponseProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PodCategoryRepository extends JpaRepository<PodCategory, UUID> {

    //  bu admin page uchun barcha categorylar orqali pod categorylarni filter qilish

    @Query(value = "      SELECT pc.*, c.id AS categoryId, c.title AS categoryTitle, a.id AS attachmentId\n" +
            "            FROM pod_category pc\n" +
            "                     JOIN categories c ON c.id = pc.category_id\n" +
            "                     JOIN attachment a on a.id = pc.photo_id\n" +
            "            WHERE (pc.category_id IN :categoryId OR (:categoryId IS NULL))\n" +
            "            ORDER BY pc.ordered;", nativeQuery = true)
    Page<List<PodCategoryResponseProjection>> getAllPodCategoriesByCategoryIds(List<UUID> categoryId, Pageable pageable);

    @Query(value = "  SELECT pc.* FROM pod_category pc\n" +
            "             JOIN categories c ON c.id = pc.category_id WHERE pc.category_id=:categoryId ORDER BY pc.ordered;", nativeQuery = true)
    List<PodCategoryProjection> getAllPodCategoriesByCategoryId(UUID categoryId);


    @Query(value = "  SELECT * FROM pod_category pc ORDER BY pc.ordered;", nativeQuery = true)
    List<PodCategoryProjection> getAllPodCategories();

    @Query(value = "SELECT * FROM pod_category pc ORDER BY pc.ordered;", nativeQuery = true)
    List<PodCategory> getAllPodCategoriesByOrder();

    @Query(value = "SELECT * FROM pod_category pc ORDER BY pc.ordered DESC;", nativeQuery = true)
    List<PodCategory> forMe();

}
