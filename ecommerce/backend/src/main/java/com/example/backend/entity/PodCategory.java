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
public class PodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String description;

    private boolean active;

//    @Column(length = 1000000)
//    private String photo;
    @ManyToOne
    private Attachment photo;

    private Timestamp createdAt;

    @ManyToOne
    private Category category;

    private Integer ordered;

}
