package com.example.backend.service.fileService;

import com.example.backend.entity.Attachment;
import com.example.backend.repositoy.AttachmentRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImgServiceImpl implements ImgService {
    private final AttachmentRepository attachmentRepository;

    // Method to get the file extension from MultipartFile
    public static String getFileExtensionFromMultipartFile(MultipartFile multipartFile) {
        String extension = "";

        if (multipartFile != null) {
            // Extract file name
            String originalFilename = multipartFile.getOriginalFilename();
            if (originalFilename != null) {
                // Find the last occurrence of '.' in the file name
                int dotIndex = originalFilename.lastIndexOf('.');
                if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                    extension = originalFilename.substring(dotIndex + 1).toLowerCase(); // Get the substring after '.'
                }
            }
        }

        return extension;
    }

    @Override
    public String uploadFile(MultipartFile photo, String prefix) throws IOException {
        String extension = getFileExtensionFromMultipartFile(photo);

        UUID uuid = UUID.randomUUID();

        // Define the directory path where you want to save the photo
        String directoryPath = "./files/images/" + prefix + "/";

        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Make directories including any necessary but nonexistent parent directories
        }

        // Define the path for the file
        String filePath = directoryPath + uuid + "." + extension;

        // Convert MultipartFile to File
        File webpFile = new File(filePath);

        try (FileOutputStream fos = new FileOutputStream(webpFile)) {
            fos.write(photo.getBytes()); // Write bytes from MultipartFile to the file
            System.out.println("File saved at: " + webpFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return uuid + "." + extension;
    }

    //    under code created by gpt
//    @Override
//    public UUID uploadFile(MultipartFile photo, String prefix) throws IOException {
//        UUID id = UUID.randomUUID();
//        try {
//
//            // Construct the file path
//            String filePath = "files/images/" + prefix + "/";
//            File directory = new File(filePath);
//
//            // Create the directory if it doesn't exist
//            if (!directory.exists()) {
//                if (!directory.mkdirs()) {
//                    throw new IOException("Failed to create the directory: " + directory);
//                }
//            }
//
//            File output = new File(filePath + id + ".webp");
//
//            // Ensure the output file is created successfully
//            if (!output.createNewFile()) {
//                throw new IOException("Failed to create the output file: " + output);
//            }
//
//            BufferedImage inputImage = ImageIO.read(photo.getInputStream());
//            BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//            outputImage.getGraphics().drawImage(inputImage, 0, 0, null);
//            ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();
//            ImageWriteParam writeParam = writer.getDefaultWriteParam();
//            writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//            writeParam.setCompressionType("Lossy");
//
//            if (photo.getSize() / 1024 > 400) {
//                writeParam.setCompressionQuality(0.5f);
//            } else {
//                writeParam.setCompressionQuality(0.8f);
//            }
//
//            // Ensure ImageOutputStream is created successfully
//            try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(output)) {
//                if (imageOutputStream == null) {
//                    throw new IOException("Failed to create ImageOutputStream.");
//                }
//
//                writer.setOutput(imageOutputStream);
//                writer.write(null, new IIOImage(outputImage, null, null), writeParam);
//            } finally {
//                writer.dispose();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return id;
//    }


    @Override
    public void getFile(String url, HttpServletResponse httpServletResponse) throws IOException {
        String id = url.substring(url.indexOf("/") + 1, url.length());
        Optional<Attachment> byId = attachmentRepository.findById(UUID.fromString(id));
        InputStream inputStream = new FileInputStream("./files/images/" + url.substring(0, url.indexOf("/")+1) + byId.get().getId() + "." + byId.get().getType());
        inputStream.transferTo(httpServletResponse.getOutputStream());
        inputStream.close();
        httpServletResponse.getOutputStream().close();

    }

    @Override
    public HttpEntity<String> deleteFiles(String url) {
        Path path = Paths.get("backend/files/images/" + url + ".webp");
        try {
            Files.delete(path);
        } catch (IOException e) {
            return ResponseEntity.ok().body("File not found!");
        }
        return ResponseEntity.ok().body("Successfully deleted!");
    }

}
