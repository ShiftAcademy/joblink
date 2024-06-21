package com.example.backend.projection;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public interface PodCategoryResponseProjection {

    UUID getId();

    String getTitle();

    String getDescription();

    Boolean getActive();

    UUID getAttachmentId();

    UUID getCategoryId();

    String getCategoryTitle();


}
