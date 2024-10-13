package com.hii.finalProject.exceptions;


import com.hii.finalProject.response.Response;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class GlobalException {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Response<HashMap<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex){
        log.error(ex.getMessage(), ex);
        HashMap<String, String> errorsMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorsMap.put(error.getField(), error.getDefaultMessage());
        });
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Unable to process the request", errorsMap);
    }
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public final ResponseEntity<Response<HashMap<String, String>>> handleConstraintValidation(MethodArgumentNotValidException ex){
        log.error(ex.getMessage(), ex);
        HashMap<String, String> errorsMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorsMap.put(error.getField(), error.getDefaultMessage());
        });
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Unable to process the request", errorsMap);
    }

    @ExceptionHandler(UniqueNameViolationException.class)
    public ResponseEntity<Response<Object>> handleUniqueNameViolationException(UniqueNameViolationException ex) {
        log.error(ex.getMessage(), ex);
        return Response.failedResponse(ex.getHttpStatus().value(), ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("File upload size exceeded", ex);
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "File size exceeds the maximum limit 2Mb. Please upload a smaller file.");
    }
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Response<Object>> handleConstraintViolationException(jakarta.validation.ConstraintViolationException ex) {
        log.error("Validation error", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Validation error");
        log.error("Constraint violation: {}", errorMessage);
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Response<String>> handleAllExceptions(Exception ex) {

        log.error(ex.getMessage(), ex);

        if (ex.getCause() instanceof UnknownHostException) {
            return Response.failedResponse(HttpStatus.NOT_FOUND.value(),
                    ex.getLocalizedMessage());
        }

        return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "We are unable to process your request at this time, please try again later.", ex.getMessage());
    }
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Response<Object>> handleDataNotFoundException(DataNotFoundException ex) {
        return Response.failedResponse(ex.getHttpStatus().value(), ex.getMessage());
    }
}
