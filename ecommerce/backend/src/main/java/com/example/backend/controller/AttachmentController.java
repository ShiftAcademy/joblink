package com.example.backend.controller;

import com.example.backend.service.fileService.AttachmentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachment")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @GetMapping("{id}")
    public void getPhoto(
            HttpServletResponse response,
            @PathVariable UUID id,
            @RequestParam(name = "prefix") String prefix
    ) throws IOException {
        attachmentService.getImage(id, response, prefix);
    }

}
