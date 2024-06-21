package com.example.backend.projection;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ProductProjection {

    UUID getId();

    String getTitle();

    String getDescription();

    Integer getPrice();

    Boolean getActive();

    UUID getAttachmentId();

    UUID getPodCategoryId();

    LocalDateTime getCreated_at();

    String getPodCategoryTitle();

}
