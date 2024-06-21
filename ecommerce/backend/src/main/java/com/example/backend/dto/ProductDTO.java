package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private String title;

    private String description;

    private Integer price;

    private boolean active;

    private String podCategory_id;

//    private String photo;

}
