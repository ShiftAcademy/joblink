package com.example.backend.service.fileService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ImgService {

    String uploadFile(MultipartFile photo, String prefix) throws IOException;

    void getFile(String url, HttpServletResponse httpServletResponse) throws IOException;

    HttpEntity<String > deleteFiles(String url);

}
