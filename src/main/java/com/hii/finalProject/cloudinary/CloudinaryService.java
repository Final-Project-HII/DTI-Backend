package com.hii.finalProject.cloudinary;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folderName);
    String generateUrl(String publicId);
    String deleteImage(String publicId) throws IOException;
}
