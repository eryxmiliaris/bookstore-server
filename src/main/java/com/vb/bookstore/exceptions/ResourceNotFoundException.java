package com.vb.bookstore.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    private String resource;
    private String field;
    private String fieldValue;
    private Long fieldValueLong;

    public ResourceNotFoundException() {

    }

    public ResourceNotFoundException(String resource, String field, String fieldValue) {
        super(String.format("%s not found with %s: %s", resource, field, fieldValue));
        this.resource = resource;
        this.field = field;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resource, String field, Long fieldValue) {
        super(String.format("%s not found with %s: %s", resource, field, fieldValue));
        this.resource = resource;
        this.field = field;
        this.fieldValueLong = fieldValue;
    }
}
