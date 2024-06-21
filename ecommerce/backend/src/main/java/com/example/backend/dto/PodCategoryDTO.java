package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PodCategoryDTO {

    private String title;

    private String description;

    private boolean active;

    private String category_id;

//    private String photo;

}
