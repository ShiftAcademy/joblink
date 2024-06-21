package com.example.backend.projection;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface UserProjection {

    UUID getId();

    String getPhone();

    Timestamp getExpiration_date();

    String getCategory_id();
    @Value("#{target.bot_active}")
    Boolean getBotActive();

}
