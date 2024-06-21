package com.example.backend.repositoy;

import com.example.backend.entity.Category;
import com.example.backend.projection.CategoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query(value = """
            SELECT * FROM categories
            """, nativeQuery = true)
    List<CategoryProjection> getCategoriesForPodCategory();

}
