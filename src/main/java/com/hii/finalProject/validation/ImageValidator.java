package com.hii.finalProject.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    private static final Logger logger = LoggerFactory.getLogger(ImageValidator.class);
    private long maxSizeInBytes;
    private Set<String> allowedContentTypes;

    @Override
    public void initialize(ValidImage constraintAnnotation) {
        this.maxSizeInBytes = constraintAnnotation.maxSizeInMB() * 1024 * 1024; // Convert MB to bytes
        this.allowedContentTypes = new HashSet<>(Arrays.asList(
                "image/jpeg", "image/png", "image/svg+xml", "image/webp"
        ));
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            logger.info("File is null or empty");
            return true; // Assume null/empty is valid, adjust if needed
        }

        logger.info("Validating file: name={}, size={}, contentType={}",
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        // Check file size
        if (file.getSize() > maxSizeInBytes) {
            String message = "File size exceeds the maximum limit of " + (maxSizeInBytes / (1024 * 1024)) + "MB";
            logger.warn(message);
            addConstraintViolation(context, message);
            return false;
        }

        // Check file content type
        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType.toLowerCase())) {
            String message = "File type not allowed. Allowed types are: JPEG, PNG, SVG, and WebP. Received: " + contentType;
            logger.warn(message);
            addConstraintViolation(context, message);
            return false;
        }

        logger.info("File validation passed");
        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}