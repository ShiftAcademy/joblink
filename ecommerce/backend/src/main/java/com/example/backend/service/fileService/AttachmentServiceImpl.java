package com.example.backend.service.fileService;

import com.example.backend.entity.Attachment;
import com.example.backend.repositoy.AttachmentRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final ImgService imageService;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, ImgService imageService) {
        this.attachmentRepository = attachmentRepository;
        this.imageService = imageService;
    }


    @Override
    public void getImage(UUID id, HttpServletResponse response, String prefix) throws IOException {
        imageService.getFile(prefix + "/" + id, response);
    }

    @SneakyThrows
    @Override
    public Attachment saveAttachment(String prefix, MultipartFile photo) {
        String uuid = imageService.uploadFile(photo, prefix);
        UUID id = UUID.fromString(uuid.substring(0,uuid.indexOf(".")));
        String extension = uuid.substring(uuid.indexOf(".")+1);
        Attachment saved = attachmentRepository.save(new Attachment(id,extension,prefix));
        return saved;
    }

    @Override
    public HttpEntity<?> deleteAttachment(UUID id, String prefix) {
        UUID id1 = attachmentRepository.findById(id).get().getId();
        imageService.deleteFiles(prefix + "/" + attachmentRepository.findById(id).get().getId());
        attachmentRepository.deleteById(id);
        return ResponseEntity.ok("Success");
    }

}
