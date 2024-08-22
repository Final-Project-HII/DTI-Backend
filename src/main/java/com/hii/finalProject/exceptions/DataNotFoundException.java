package com.hii.finalProject.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataNotFoundException extends RuntimeException {
    private HttpStatus httpStatus;
    private List<String> errors;
    private Object data;

    public DataNotFoundException(String message){
        this(org.springframework.http.HttpStatus.BAD_REQUEST,message);
    }

    public DataNotFoundException(HttpStatus httpStatus, String message) {
        this(httpStatus, message, Collections.singletonList(message), null);
    }

    public DataNotFoundException(HttpStatus httpStatus, String message, List<String> errors, Object data) {
        super(message);
        this.httpStatus = httpStatus;
        this.errors = errors;
        this.data = data;
    }
}
