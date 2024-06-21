package com.example.backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String description;

    private Integer price;

    @ManyToOne
    @JoinColumn(name = "pod_category_id")
    private PodCategory podCategory;

    private boolean active;

    //    @Column(length = 1000000)
//    private String photo;
    @ManyToOne
    private Attachment photo;

    private Integer ordered;

    private Timestamp createdAt;
}
