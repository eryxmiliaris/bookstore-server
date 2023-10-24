package com.vb.bookstore.exceptions;

import com.vb.bookstore.payloads.MessageResponse;
import com.vb.bookstore.security.jwt.AuthEntryPointJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageResponse> handleBadCredentialsException(BadCredentialsException e) {
        logger.error("Bad credentials error: {}", e.getMessage());
        String message = e.getMessage();
        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        logger.error("Resource not found error: {}", e.getMessage());
        String message = e.getMessage();
        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setSuccess(false);
        messageResponse.setMessage("Validation failed");
        messageResponse.setErrors(errors);
        return ResponseEntity.badRequest()
                .body(messageResponse);
    }
    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<MessageResponse> handleApiRequestException(ApiRequestException e) {
        logger.error("Api request error: {}", e.getMessage());
        MessageResponse response = new MessageResponse();
        response.setSuccess(false);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response, e.getStatus());
    }
}
