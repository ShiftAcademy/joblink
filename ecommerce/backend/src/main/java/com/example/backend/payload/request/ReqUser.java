package com.example.backend.payload.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUser {

    private String phone;

    private String password;

    private String category_id;

    private String role_name;

    private Timestamp expiration_date;
}
