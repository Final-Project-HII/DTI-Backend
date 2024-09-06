package com.hii.finalProject.cloudinary;

import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    @Resource
    private Cloudinary cloudinary;
    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        try{
            byte[] bytes = file.getBytes();
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);
            Map uploadedFile = cloudinary.uploader().upload(bytes, options);
            String publicId = (String) uploadedFile.get("secure_url");
            return publicId;
        }catch (IOException e){
            throw new RuntimeException("Error uploading file to Cloudinary", e);
        }
    }

    @Override
    public String generateUrl(String publicId) {
        return cloudinary.url().secure(true).generate(publicId);
    }

    @Override
    public String deleteImage(String publicId) throws IOException {
        Map result = cloudinary.uploader().destroy(publicId, Map.of());
        return (String) result.get("result");
    }
}
