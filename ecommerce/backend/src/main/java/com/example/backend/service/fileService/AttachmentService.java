package com.example.backend.service.fileService;

import com.example.backend.entity.Attachment;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface AttachmentService {

    void getImage(UUID id, HttpServletResponse response, String prefix) throws IOException;

    Attachment saveAttachment(String prefix, MultipartFile file);

    HttpEntity<?> deleteAttachment(UUID id, String prefix);

}
