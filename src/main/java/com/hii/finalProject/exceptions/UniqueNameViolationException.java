package com.hii.finalProject.exceptions;

import org.springframework.http.HttpStatus;

public class UniqueNameViolationException extends DataNotFoundException {
    private final String entityName;

    public UniqueNameViolationException(String entityName, String name) {
        super(HttpStatus.BAD_REQUEST, String.format("A %s with the name '%s' already exists, name must be unique", entityName, name));
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }
}